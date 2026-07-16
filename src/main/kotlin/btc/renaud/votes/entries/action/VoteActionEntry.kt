package btc.renaud.votes.entries.action

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.extension.annotations.*
import com.typewritermc.core.interaction.context
import com.typewritermc.engine.paper.entry.Criteria
import com.typewritermc.engine.paper.entry.Modifier
import com.typewritermc.engine.paper.entry.TriggerableEntry
import com.typewritermc.engine.paper.entry.entries.ActionEntry
import com.typewritermc.engine.paper.entry.entries.ActionTrigger
import com.typewritermc.engine.paper.entry.triggerAllFor
import org.koin.java.KoinJavaComponent.get
import btc.renaud.votes.entries.manifest.VoteDefinitionEntry
import btc.renaud.votes.services.VoteService

@Entry("vote_action", "Vote Action", Colors.RED, "fa6-solid:check-to-slot")
@Tags("vote_action")
class VoteActionEntry(
    override val id: String = "",
    override val name: String = "",
    override val criteria: List<Criteria> = emptyList(),
    override val modifiers: List<Modifier> = emptyList(),
    override val triggers: List<Ref<TriggerableEntry>> = emptyList(),
    @Help("The vote definition to cast a vote on")
    val definition: Ref<VoteDefinitionEntry> = emptyRef(),
    @Help("The option index (0-based) to vote for")
    val optionIndex: Int = 0,
) : ActionEntry {
    override fun ActionTrigger.execute() {
        val def = definition.get() ?: return
        val voteService = get<VoteService>(VoteService::class.java)
        if (voteService.vote(player, def, optionIndex)) {
            triggerAllFor(player, context())
        }
    }
}
