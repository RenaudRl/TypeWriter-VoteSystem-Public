package btc.renaud.votes

import com.typewritermc.core.entries.Query
import com.typewritermc.core.extension.annotations.Singleton
import com.typewritermc.engine.paper.extensions.placeholderapi.PlaceholderHandler
import org.bukkit.entity.Player

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

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        if (!params.startsWith("vote_", ignoreCase = true)) return null

        optionPattern.matchEntire(params)?.let {
            val (id, idxStr) = it.destructured
            val def = resolveDefinition(id) ?: return null
            val index = idxStr.toIntOrNull()?.minus(1) ?: return null
            return VoteManager.optionText(def, index, player)
        }

        displayPattern.matchEntire(params)?.let {
            val (id) = it.destructured
            val def = resolveDefinition(id) ?: return null
            return VoteManager.displayName(def, player)
        }

        totalPattern.matchEntire(params)?.let {
            val (id) = it.destructured
            val def = resolveDefinition(id) ?: return null
            return VoteManager.totalVotes(def).toString()
        }

        statsPattern.matchEntire(params)?.let {
            val (id, idxStr) = it.destructured
            val def = resolveDefinition(id) ?: return null
            val counts = VoteManager.optionVotes(def)
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
            val option = VoteManager.playerOption(player, def) ?: return ""
            return VoteManager.optionText(def, option, player)
        }

        return null
    }

    private fun resolveDefinition(id: String): VoteDefinitionEntry? {
        return VoteManager.definition(id) ?: Query.findById<VoteDefinitionEntry>(id)?.also {
            VoteManager.registerDefinition(it)
        }
    }
}
