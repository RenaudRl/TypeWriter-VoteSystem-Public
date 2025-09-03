package btc.renaud.votes

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.typewritermc.engine.paper.entry.entries.get
import org.bukkit.entity.Player
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

object VoteManager {
    private val definitions = ConcurrentHashMap<String, VoteDefinitionEntry>()

    fun registerDefinition(entry: VoteDefinitionEntry) {
        definitions[entry.id.lowercase()] = entry
    }

    fun definition(id: String): VoteDefinitionEntry? = definitions[id.lowercase()]

    fun vote(player: Player, definition: VoteDefinitionEntry, option: VoteOption): Boolean {
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
        val data = artifact.loadDefinitionData(definition.id)
        val players = data.get("players")?.asJsonObject ?: JsonObject().also { data.add("players", it) }
        val uuid = player.uniqueId.toString()
        if (players.has(uuid)) return false
        val options = data.get("options")?.asJsonArray ?: JsonArray().also { data.add("options", it) }
        while (options.size() < definition.options.size) {
            options.add(0)
        }
        val index = option.index
        if (index < 0 || index >= options.size()) return false
        val current = options[index].asInt
        options[index] = JsonPrimitive(current + 1)
        val total = data["total"]?.asInt ?: 0
        data.addProperty("total", total + 1)
        players.addProperty(uuid, index)
        // Persist the updated vote information for this definition
        artifact.saveDefinitionData(definition.id, data)
        return true
    }

    fun hasVoted(player: Player, definition: VoteDefinitionEntry): Boolean {
        val artifact = definition.data.get() ?: return false
        val data = artifact.loadDefinitionData(definition.id)
        val players = data.get("players")?.asJsonObject ?: return false
        return players.has(player.uniqueId.toString())
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

    fun allDefinitions(): Collection<VoteDefinitionEntry> = definitions.values
}
