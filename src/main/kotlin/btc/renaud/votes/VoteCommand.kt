package btc.renaud.votes

import btc.renaud.votes.command.entryCompat
import com.typewritermc.core.extension.annotations.TypewriterCommand
import com.typewritermc.engine.paper.command.dsl.CommandTree
import com.typewritermc.engine.paper.command.dsl.executePlayerOrTarget
import com.typewritermc.engine.paper.command.dsl.int
import com.typewritermc.engine.paper.command.dsl.sender
import com.typewritermc.engine.paper.command.dsl.withPermission
import com.typewritermc.engine.paper.utils.msg

@TypewriterCommand
fun CommandTree.voteCommand() = literal("vote") {
    withPermission("typewriter.vote")

    executes {
        sender.msg("Usage: /tw vote <definition> <option> [target]")
    }

    entryCompat("definition", VoteDefinitionEntry::class) { defArg ->
        withPermission("typewriter.vote.cast")
        int("option", 1) { optionArg ->
            executePlayerOrTarget { voter ->
                val definition = defArg()
                val index = optionArg() - 1
                val option = VoteOption.availableOptions(definition).getOrNull(index)
                if (option == null) {
                    sender.msg("Option ${optionArg()} is not available for ${definition.id}.")
                    return@executePlayerOrTarget
                }

                if (VoteManager.hasVoted(voter, definition)) {
                    sender.msg("${voter.name} has already voted in ${definition.id}.")
                    return@executePlayerOrTarget
                }

                if (VoteManager.vote(voter, definition, option)) {
                    val label = VoteManager.optionText(definition, index, voter).ifBlank {
                        "option ${index + 1}"
                    }
                    voter.msg("Your vote for <blue>${definition.id}</blue> has been recorded as <green>$label</green>.")
                    if (sender != voter) {
                        sender.msg("Recorded vote for <blue>${voter.name}</blue> in ${definition.id}: $label.")
                    } else {
                        sender.msg("Vote registered for ${definition.id}: $label.")
                    }
                } else {
                    sender.msg("Unable to register the vote for ${voter.name} in ${definition.id}.")
                }
            }
        }
    }

    literal("reset") {
        withPermission("typewriter.vote.reset")
        entryCompat("definition", VoteDefinitionEntry::class) { defArg ->
            executes {
                val definition = defArg()
                VoteManager.reset(definition)
                sender.msg("Votes reset for ${definition.id}.")
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
            val definitions = VoteManager.allDefinitions()
            if (definitions.isEmpty()) {
                sender.msg("There are no active vote definitions.")
                return@executes
            }

            definitions.sortedBy { it.id }.forEach { definition ->
                sender.msg(formatVoteStats(definition))
            }
        }
    }
}

private fun formatVoteStats(definition: VoteDefinitionEntry): String {
    val displayName = VoteManager.displayName(definition, null).ifBlank {
        definition.name.ifBlank { definition.id }
    }
    val optionStats = VoteManager.optionVotes(definition)
        .mapIndexed { index, count ->
            val label = VoteManager.optionText(definition, index, null).ifBlank {
                "Option ${index + 1}"
            }
            "${index + 1}: $count ($label)"
        }
        .joinToString(", ")
    val total = VoteManager.totalVotes(definition)
    return "Stats for $displayName [${definition.id}]: $optionStats | total: $total"
}

