package btc.renaud.votes.entries.action

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.extension.annotations.*
import com.typewritermc.engine.paper.entry.Criteria
import com.typewritermc.engine.paper.entry.Modifier
import com.typewritermc.engine.paper.entry.TriggerableEntry
import com.typewritermc.engine.paper.entry.entries.ActionEntry
import com.typewritermc.engine.paper.entry.entries.ActionTrigger
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var
import org.koin.java.KoinJavaComponent.get
import btc.renaud.votes.entries.manifest.VoteDefinitionEntry
import btc.renaud.votes.services.VoteService

@Entry("vote_message", "Vote Message", Colors.RED, "fa6-solid:envelope")
@Tags("vote_message")
class VoteMessageActionEntry(
    override val id: String = "",
    override val name: String = "",
    override val criteria: List<Criteria> = emptyList(),
    override val modifiers: List<Modifier> = emptyList(),
    override val triggers: List<Ref<TriggerableEntry>> = emptyList(),
    @Help("The vote definition to check")
    val definition: Ref<VoteDefinitionEntry> = emptyRef(),
    @Placeholder
    @Colored
    @MultiLine
    @Help("Message to send if the player has NOT yet voted")
    val notVotedMessage: Var<String> = ConstVar(""),
    @Placeholder
    @Colored
    @MultiLine
    @Help("Message to send if the player HAS already voted (leave empty for no message)")
    val alreadyVotedMessage: Var<String> = ConstVar(""),
) : ActionEntry {
    override fun ActionTrigger.execute() {
        val def = definition.get() ?: return
        val voteService = get<VoteService>(VoteService::class.java)
        if (voteService.hasVoted(player, def)) {
            val msg = alreadyVotedMessage.get(player)
            if (msg.isNotBlank()) {
                player.sendMessage(msg)
            }
        } else {
            val msg = notVotedMessage.get(player)
            if (msg.isNotBlank()) {
                player.sendMessage(msg)
            }
        }
    }
}
