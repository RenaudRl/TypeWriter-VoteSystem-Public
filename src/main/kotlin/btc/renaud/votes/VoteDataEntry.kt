package btc.renaud.votes

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.Tags
import com.typewritermc.engine.paper.entry.entries.ArtifactEntry
import java.util.UUID

@Entry("vote_data", "Vote Data", Colors.BLUE, "fa6-solid:database")
@Tags("vote_data")
class VoteDataEntry(
    override val id: String = "",
    override val name: String = "",
    override val artifactId: String = UUID.randomUUID().toString(),
) : ArtifactEntry
