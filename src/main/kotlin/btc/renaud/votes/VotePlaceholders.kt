package btc.renaud.votes

import btc.renaud.votes.entries.manifest.VoteDefinitionEntry
import btc.renaud.votes.services.VoteService
import com.typewritermc.core.entries.Query
import com.typewritermc.core.extension.annotations.Singleton
import com.typewritermc.engine.paper.extensions.placeholderapi.PlaceholderHandler
import org.bukkit.entity.Player
import org.koin.java.KoinJavaComponent

@Singleton
class VotePlaceholders : PlaceholderHandler {
    // Patterns mirror the behaviour of other extensions (see Docs/deacoudre)
    // and allow identifiers containing ":" or "-" characters.
    private val optionPattern =
        Regex("""^vote_option_([a-zA-Z0-9_:-]+)_(\d+)$""", RegexOption.IGNORE_CASE)
    private val displayPattern =
        Regex("""^vote_display_([a-zA-Z0-9_:-]+)$""", RegexOption.IGNORE_CASE)
    private val totalPattern =
        Regex("""^vote_total_([a-zA-Z0-9_:-]+)$""", RegexOption.IGNORE_CASE)
    private val statsPattern =
        Regex("""^vote_(?:stats|votes)_([a-zA-Z0-9_:-]+)(?:_(\d+))?$""", RegexOption.IGNORE_CASE)
    private val playerPattern =
        Regex("""^vote_player_([a-zA-Z0-9_:-]+)$""", RegexOption.IGNORE_CASE)

    private val voteService: VoteService
        get() = KoinJavaComponent.get(VoteService::class.java)

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        if (!params.startsWith("vote_", ignoreCase = true)) return null

        optionPattern.matchEntire(params)?.let {
            val (id, idxStr) = it.destructured
            val def = resolveDefinition(id) ?: return null
            val index = idxStr.toIntOrNull()?.minus(1) ?: return null
            return voteService.optionText(def, index, player)
        }

        displayPattern.matchEntire(params)?.let {
            val (id) = it.destructured
            val def = resolveDefinition(id) ?: return null
            return voteService.displayName(def, player)
        }

        totalPattern.matchEntire(params)?.let {
            val (id) = it.destructured
            val def = resolveDefinition(id) ?: return null
            return voteService.totalVotes(def).toString()
        }

        statsPattern.matchEntire(params)?.let {
            val (id, idxStr) = it.destructured
            val def = resolveDefinition(id) ?: return null
            val counts = voteService.optionVotes(def)
            if (idxStr.isNotBlank()) {
                val index = idxStr.toIntOrNull()?.minus(1) ?: return null
                return counts.getOrNull(index)?.toString() ?: "0"
            }
            return counts.joinToString(",")
        }

        playerPattern.matchEntire(params)?.let {
            val (id) = it.destructured
            if (player == null) return null
            val def = resolveDefinition(id) ?: return null
            val option = voteService.playerOption(player, def) ?: return ""
            return voteService.optionText(def, option, player)
        }

        return null
    }

    private fun resolveDefinition(id: String): VoteDefinitionEntry? {
        return voteService.definition(id) ?: Query.findById<VoteDefinitionEntry>(id)?.also {
            voteService.registerDefinition(it)
        }
    }
}
