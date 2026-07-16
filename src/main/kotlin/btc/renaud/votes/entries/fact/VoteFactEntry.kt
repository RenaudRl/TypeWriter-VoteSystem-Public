package btc.renaud.votes.entries.fact

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.extension.annotations.*
import com.typewritermc.engine.paper.entry.entries.CachableFactEntry
import com.typewritermc.engine.paper.entry.entries.GroupEntry

@Entry("vote_fact", "Vote Fact", Colors.PURPLE, "mingcute:counter-fill")
@Tags("vote_fact")
class VoteFactEntry(
    override val id: String = "",
    override val name: String = "",
    @Help("Description of what this fact tracks")
    @MultiLine
    override val comment: String = "",
    override val group: Ref<GroupEntry> = emptyRef(),
) : CachableFactEntry
