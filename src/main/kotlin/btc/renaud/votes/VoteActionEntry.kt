package btc.renaud.votes

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.Tags
import com.typewritermc.engine.paper.entry.Criteria
import com.typewritermc.engine.paper.entry.Modifier
import com.typewritermc.engine.paper.entry.TriggerableEntry
import com.typewritermc.engine.paper.entry.entries.ActionEntry
import com.typewritermc.engine.paper.entry.entries.ActionTrigger
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var

@Entry("vote_action", "Vote Action", Colors.GREEN, "fa6-solid:check-to-slot")
@Tags("vote_action")
class VoteActionEntry(
    override val id: String = "",
    override val name: String = "",
    override val criteria: List<Criteria> = emptyList(),
    override val modifiers: List<Modifier> = emptyList(),
    override val triggers: List<Ref<TriggerableEntry>> = emptyList(),
    val definition: Ref<VoteDefinitionEntry> = emptyRef(),
    val option: Var<VoteOption> = ConstVar(VoteOption.OPTION_1),
) : ActionEntry {
    override fun ActionTrigger.execute() {
        val def = definition.get() ?: return
        val opt = option.get(player)
        VoteManager.vote(player, def, opt)
    }
}

