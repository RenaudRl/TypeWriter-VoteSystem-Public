package btc.renaud.votes.entries.manifest

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.Help
import com.typewritermc.core.extension.annotations.Min
import com.typewritermc.core.extension.annotations.Tags
import com.typewritermc.engine.paper.entry.ManifestEntry

@Entry("vote_config", "Vote System Configuration", Colors.ORANGE, "mdi:cog")
@Tags("vote_config")
class VoteConfigEntry(
    override val id: String = "default",
    override val name: String = "vote_config",
    @Help("Enable debug logging for vote operations")
    val debug: Boolean = false,
    @Min(0)
    @Help("0 means one vote per poll; a positive value allows another vote after this many seconds")
    val cooldownSeconds: Int = 0,
) : ManifestEntry
