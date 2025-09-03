package btc.renaud.votes

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.extension.annotations.*
import com.typewritermc.engine.paper.entry.ManifestEntry
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var

@Entry("vote_definition", "Vote Definition", Colors.ORANGE, "fa6-solid:check")
@Tags("vote_definition")
class VoteDefinitionEntry(
    override val id: String = "",
    override val name: String = "",
    @Placeholder
    @Colored
    @MultiLine
    val displayName: Var<String> = ConstVar(""),
    @Placeholder
    @Colored
    @MultiLine
    val options: List<Var<String>> = emptyList(),
    @Help("ISO-8601 formatted end date")
    val endDate: String = "",
    @Placeholder
    @Colored
    @MultiLine
    @Help("Message shown when voting is closed")
    val closedMessage: Var<String> = ConstVar(""),
    @Help("Artifact storing vote data")
    val data: Ref<VoteDataEntry> = emptyRef(),
) : ManifestEntry {
    init {
        VoteManager.registerDefinition(this)
    }
}
