package btc.renaud.votes

import btc.renaud.votes.command.entryCompat
import btc.renaud.votes.entries.manifest.VoteDefinitionEntry
import btc.renaud.votes.services.VoteService
import com.typewritermc.core.extension.annotations.TypewriterCommand
import com.typewritermc.engine.paper.command.dsl.CommandTree
import com.typewritermc.engine.paper.command.dsl.executePlayerOrTarget
import com.typewritermc.engine.paper.command.dsl.int
import com.typewritermc.engine.paper.command.dsl.sender
import com.typewritermc.engine.paper.command.dsl.withPermission
import com.typewritermc.engine.paper.utils.msg
import org.koin.java.KoinJavaComponent

private val voteService: VoteService
    get() = KoinJavaComponent.get(VoteService::class.java)

@TypewriterCommand
fun CommandTree.voteCommand() = literal("vote") {
    withPermission("typewriter.vote")

    executes {
        sender.msg(VoteTexts.usage())
    }

    entryCompat("definition", VoteDefinitionEntry::class) { defArg ->
        withPermission("typewriter.vote.cast")
        int("option", 1) { optionArg ->
            executePlayerOrTarget { voter ->
                val definition = defArg()
                val index = optionArg() - 1
                if (index !in definition.options.indices) {
                    sender.msg(VoteTexts.optionUnavailable(optionArg(), definition.id))
                    return@executePlayerOrTarget
                }

                if (voteService.hasVoted(voter, definition)) {
                    sender.msg(VoteTexts.alreadyVoted(voter.name, definition.id))
                    return@executePlayerOrTarget
                }

                if (voteService.vote(voter, definition, index)) {
                    val label = voteService.optionText(definition, index, voter).ifBlank {
                        VoteTexts.castOptionFallback(index)
                    }
                    voter.msg(VoteTexts.recordedForVoter(definition.id, label))
                    if (sender != voter) {
                        sender.msg(VoteTexts.recordedByOther(voter.name, definition.id, label))
                    } else {
                        sender.msg(VoteTexts.recordedForSelf(definition.id, label))
                    }
                } else {
                    sender.msg(VoteTexts.castFailed(voter.name, definition.id))
                }
            }
        }
    }

    literal("reset") {
        withPermission("typewriter.vote.reset")
        entryCompat("definition", VoteDefinitionEntry::class) { defArg ->
            executes {
                val definition = defArg()
                voteService.reset(definition)
                sender.msg(VoteTexts.resetDone(definition.id))
            }
        }
    }

    literal("stats") {
        withPermission("typewriter.vote.stats")
        entryCompat("definition", VoteDefinitionEntry::class) { defArg ->
            executes {
                sender.msg(formatVoteStats(defArg()))
            }
        }
        executes {
            val definitions = voteService.allDefinitions()
            if (definitions.isEmpty()) {
                sender.msg(VoteTexts.noDefinitions())
                return@executes
            }

            definitions.sortedBy { it.id }.forEach { definition ->
                sender.msg(formatVoteStats(definition))
            }
        }
    }
}

private fun formatVoteStats(definition: VoteDefinitionEntry): String {
    val displayName = voteService.displayName(definition, null).ifBlank {
        definition.name.ifBlank { definition.id }
    }
    val counts = voteService.optionVotes(definition)
    val labels = counts.indices.map { index -> voteService.optionText(definition, index, null) }
    return VoteTexts.stats(displayName, definition.id, counts, labels, voteService.totalVotes(definition))
}
