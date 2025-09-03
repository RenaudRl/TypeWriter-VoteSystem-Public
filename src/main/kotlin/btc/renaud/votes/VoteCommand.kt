package btc.renaud.votes

import com.typewritermc.core.extension.annotations.TypewriterCommand
import com.typewritermc.engine.paper.command.dsl.*
import com.typewritermc.engine.paper.utils.msg

@TypewriterCommand
fun CommandTree.voteCommand() = literal("vote") {
    withPermission("typewriter.vote")
    entry<VoteDefinitionEntry>("definition") { defArg ->
        int("option", 1, 10) { optionArg ->
            executePlayerOrTarget { voter ->
                val def = defArg()
                val index = optionArg() - 1
                val option = VoteOption.availableOptions(def).getOrNull(index)
                if (option == null) {
                    sender.msg("Invalid option.")
                    return@executePlayerOrTarget
                }
                if (VoteManager.hasVoted(voter, def)) {
                    sender.msg("You have already voted.")
                    return@executePlayerOrTarget
                }
                if (VoteManager.vote(voter, def, option)) {
                    sender.msg("Vote registered for option ${index + 1}.")
                }
            }
        }
    }

    literal("reset") {
        entry<VoteDefinitionEntry>("definition") { defArg ->
            executes {
                VoteManager.reset(defArg())
                sender.msg("Votes reset for ${defArg().id}.")
            }
        }
    }

    literal("stats") {
        entry<VoteDefinitionEntry>("definition") { defArg ->
            executes {
                val def = defArg()
                val counts = VoteManager.optionVotes(def)
                val line = counts.mapIndexed { idx, c -> "${idx + 1}: $c" }.joinToString(", ")
                sender.msg("Stats for ${def.id}: $line")
            }
        }
        executes {
            VoteManager.allDefinitions().forEach { def ->
                val counts = VoteManager.optionVotes(def)
                val line = counts.mapIndexed { idx, c -> "${idx + 1}: $c" }.joinToString(", ")
                sender.msg("${def.id}: $line")
            }
        }
    }
}
