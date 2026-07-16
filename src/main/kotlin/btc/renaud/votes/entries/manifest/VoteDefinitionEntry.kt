package btc.renaud.votes.entries.manifest

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.extension.annotations.*
import com.typewritermc.engine.paper.entry.ManifestEntry
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.Plugin
import btc.renaud.votes.entries.artifact.VoteDataEntry
import btc.renaud.votes.services.VoteService
import org.koin.java.KoinJavaComponent.get

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
    @Help("ISO-8601 formatted end date (e.g. 2025-01-01T00:00:00Z). Leave empty for no end date.")
    val endDate: String = "",
    @Placeholder
    @Colored
    @MultiLine
    @Help("Message shown when voting is closed")
    val closedMessage: Var<String> = ConstVar(""),
    @Help("Artifact storing vote data")
    val data: Ref<VoteDataEntry> = emptyRef(),
) : ManifestEntry, org.bukkit.event.Listener {

    companion object {
        private val plugin: Plugin = Bukkit.getPluginManager().getPlugin("Typewriter")
            ?: throw IllegalStateException("Typewriter plugin not found")
    }

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
        try {
            val voteService = get<VoteService>(VoteService::class.java)
            voteService.registerDefinition(this)
        } catch (_: Exception) {
            // Service may not be available yet during static init
        }
    }

    @org.bukkit.event.EventHandler
    fun onUnload(event: org.bukkit.event.server.PluginDisableEvent) {
        HandlerList.unregisterAll(this)
    }
}
