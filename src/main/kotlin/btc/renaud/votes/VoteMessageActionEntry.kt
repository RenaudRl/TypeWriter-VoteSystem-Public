package btc.renaud.votes

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

@Entry("vote_message", "Vote Message", Colors.BLUE, "fa6-solid:envelope")
@Tags("vote_message")
class VoteMessageActionEntry(
    override val id: String = "",
    override val name: String = "",
    override val criteria: List<Criteria> = emptyList(),
    override val modifiers: List<Modifier> = emptyList(),
    override val triggers: List<Ref<TriggerableEntry>> = emptyList(),
    val definition: Ref<VoteDefinitionEntry> = emptyRef(),
    @Placeholder
    @Colored
    @MultiLine
    val message: Var<String> = ConstVar(""),
) : ActionEntry {
    override fun ActionTrigger.execute() {
        val def = definition.get() ?: return
        if (!VoteManager.hasVoted(player, def)) {
            val msg = message.get(player)
            if (msg.isNotBlank()) {
                player.sendMessage(msg)
            }
        }
    }
}

