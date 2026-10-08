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
import org.koin.java.KoinJavaComponent.get
import btc.renaud.votes.entries.manifest.VoteDefinitionEntry
import btc.renaud.votes.sendPollMessage
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
    @Help("Triggered instead of the triggers above when the vote is refused (poll closed, cooldown not over, ...). The triggers above and the modifiers only run when the vote is recorded. The player is also shown the closed or blocked message of the Vote Definition.")
    val refusedTriggers: List<Ref<TriggerableEntry>> = emptyList(),
) : ActionEntry {
    override fun ActionTrigger.execute() {
        // The engine would otherwise fire `triggers` and apply `modifiers` after every attempt, refused ones included.
        disableAutomaticTriggering()

        val def = definition.get() ?: return
        val voteService = get<VoteService>(VoteService::class.java)

        val outcome = voteService.vote(player, def, optionIndex)
        if (outcome.isAccepted) {
            triggerManually()
            return
        }

        player.sendPollMessage(voteService.refusalText(def, outcome, player))
        refusedTriggers.triggerFor(player)
    }
}
