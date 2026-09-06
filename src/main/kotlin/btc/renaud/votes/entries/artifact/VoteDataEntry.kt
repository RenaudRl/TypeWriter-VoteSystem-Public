package btc.renaud.votes.entries.artifact

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.Help
import com.typewritermc.core.extension.annotations.Tags
import com.typewritermc.engine.paper.entry.entries.ArtifactEntry

@Entry("vote_data", "Vote Data", Colors.BLUE, "fa6-solid:database")
@Tags("vote_data")
class VoteDataEntry(
    override val id: String = "",
    override val name: String = "",
    @Help("Unique identifier for this vote storage. Use a fixed name to preserve data across reloads.")
    override val artifactId: String = "vote_storage",
) : ArtifactEntry
