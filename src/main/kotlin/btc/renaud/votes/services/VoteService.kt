package btc.renaud.votes.services

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.typewritermc.core.entries.Query
import com.typewritermc.core.extension.annotations.Singleton
import com.typewritermc.engine.paper.entry.entries.ArtifactEntry
import com.typewritermc.engine.paper.entry.entries.get
import com.typewritermc.engine.paper.plugin
import org.bukkit.entity.Player
import btc.renaud.votes.entries.manifest.VoteConfigEntry
import btc.renaud.votes.entries.manifest.VoteDefinitionEntry
import btc.renaud.votes.loadDefinitionData
import btc.renaud.votes.saveDefinitionData
import btc.renaud.votes.removeDefinitionData
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Singleton
class VoteService {
    private val definitions = ConcurrentHashMap<String, VoteDefinitionEntry>()

    // Definitions register themselves when their entry is built, so only the config is (re)loaded here.
    @Volatile
    private var config: VoteConfigEntry? = null

    /** Reads the `vote_config` entry: the one named `default` if several exist, otherwise the first. */
    fun initialize() {
        val configs = Query.find<VoteConfigEntry>().toList()
        config = configs.firstOrNull { it.id == "default" } ?: configs.firstOrNull()
    }

    fun shutdown() {
        config = null
    }

    fun registerDefinition(entry: VoteDefinitionEntry) {
        definitions[entry.id.lowercase()] = entry
    }

    fun definition(id: String): VoteDefinitionEntry? = definitions[id.lowercase()]

    fun allDefinitions(): Collection<VoteDefinitionEntry> = definitions.values

    // Serialized: the check and the write below read and rewrite the same artifact file.
    @Synchronized
    fun vote(player: Player, definition: VoteDefinitionEntry, optionIndex: Int): Boolean {
        if (optionIndex < 0 || optionIndex >= definition.options.size) return false

        val artifact = definition.data.get() ?: return false

        if (definition.endDate.isNotBlank()) {
            runCatching { Instant.parse(definition.endDate) }.getOrNull()?.let {
                if (Instant.now().isAfter(it)) {
                    val msg = definition.closedMessage.get(player)
                    if (!msg.isNullOrBlank()) {
                        player.sendMessage(msg)
                    }
                    return false
                }
            }
        }

        val now = System.currentTimeMillis()
        val data = loadStamped(artifact, definition, now)
        val uuid = player.uniqueId.toString()

        if (VoteTally.isBlocked(VoteTally.previousCastAt(data, uuid), cooldownSeconds(), now)) {
            debug("vote ${definition.id} by ${player.name} refused: still blocked")
            return false
        }

        VoteTally.record(data, uuid, optionIndex, definition.options.size, now)
        artifact.saveDefinitionData(definition.id, data)
        debug("vote ${definition.id} by ${player.name} recorded for option $optionIndex")
        return true
    }

    /** True while the player cannot vote again: always after a vote, or until the cooldown elapses. */
    @Synchronized
    fun hasVoted(player: Player, definition: VoteDefinitionEntry): Boolean {
        val artifact = definition.data.get() ?: return false
        val now = System.currentTimeMillis()
        val data = loadStamped(artifact, definition, now)
        val previous = VoteTally.previousCastAt(data, player.uniqueId.toString())
        return VoteTally.isBlocked(previous, cooldownSeconds(), now)
    }

    /**
     * Loads a poll and dates the votes saved before `castAt` existed, persisting the migration.
     * Whichever of [vote] or [hasVoted] touches the poll first after the update starts those
     * cooldowns; callers hold the service lock because this may write the artifact.
     */
    private fun loadStamped(artifact: ArtifactEntry, definition: VoteDefinitionEntry, now: Long): JsonObject {
        val data = artifact.loadDefinitionData(definition.id)
        if (VoteTally.stampLegacy(data, now)) {
            artifact.saveDefinitionData(definition.id, data)
            debug("poll ${definition.id}: dated legacy votes at migration")
        }
        return data
    }

    fun playerOption(player: Player, definition: VoteDefinitionEntry): Int? {
        val artifact = definition.data.get() ?: return null
        val data = artifact.loadDefinitionData(definition.id)
        val players = data.get("players")?.asJsonObject ?: return null
        return players[player.uniqueId.toString()]?.asInt
    }

    fun totalVotes(definition: VoteDefinitionEntry): Int {
        val artifact = definition.data.get() ?: return 0
        val data = artifact.loadDefinitionData(definition.id)
        return data["total"]?.asInt ?: 0
    }

    fun optionVotes(definition: VoteDefinitionEntry): List<Int> {
        val artifact = definition.data.get() ?: return List(definition.options.size) { 0 }
        val data = artifact.loadDefinitionData(definition.id)
        val options = data.get("options")?.asJsonArray ?: JsonArray()
        return (0 until definition.options.size).map { idx ->
            if (idx < options.size()) options[idx].asInt else 0
        }
    }

    fun optionText(definition: VoteDefinitionEntry, index: Int, player: Player?): String {
        return definition.options.getOrNull(index)?.get(player) ?: ""
    }

    fun displayName(definition: VoteDefinitionEntry, player: Player?): String {
        return definition.displayName.get(player) ?: ""
    }

    fun reset(definition: VoteDefinitionEntry) {
        val artifact = definition.data.get() ?: return
        artifact.removeDefinitionData(definition.id)
    }

    private fun cooldownSeconds(): Int = config?.cooldownSeconds?.coerceAtLeast(0) ?: 0

    private fun debug(message: String) {
        if (config?.debug == true) plugin.logger.info("[VoteSystem] $message")
    }
}
