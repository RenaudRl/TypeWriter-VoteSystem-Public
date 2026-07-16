package btc.renaud.votes

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.typewritermc.engine.paper.entry.entries.ArtifactEntry
import org.bukkit.Bukkit
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.get
import java.io.File
import java.util.UUID

private val gson: Gson
    get() = get(Gson::class.java, named("dataSerializer"))
private val pluginFolder: File?
    get() = Bukkit.getPluginManager().getPlugin("Typewriter")?.dataFolder

/**
 * Loads the JSON data stored in this artifact. If no data has been stored yet
 * an empty object is returned without creating the underlying asset.
 */
fun ArtifactEntry.loadData(): JsonObject {
    if (artifactId.isBlank()) return JsonObject()
    val folder = pluginFolder ?: return JsonObject()
    val base = File(folder, "assets").canonicalFile
    val file = File(base, path).canonicalFile
    if (!file.path.startsWith(base.path) || !file.exists()) return JsonObject()
    return try {
        gson.fromJson(file.readText(), JsonObject::class.java) ?: JsonObject()
    } catch (_: Throwable) {
        JsonObject()
    }
}

/**
 * Persists the given JSON object to this artifact. The asset is only created
 * or updated when data is explicitly saved.
 */
fun ArtifactEntry.saveData(obj: JsonObject) {
    if (artifactId.isBlank()) return
    val folder = pluginFolder ?: return
    val base = File(folder, "assets").canonicalFile
    val file = File(base, path).canonicalFile
    if (!file.path.startsWith(base.path)) return
    file.parentFile.mkdirs()
    try {
        file.writeText(gson.toJson(obj))
    } catch (_: Throwable) {
        // Ignore persistence errors
    }
}

/**
 * Loads data stored for a specific player within this artifact. If no data
 * exists for that player an empty object is returned.
 */
fun ArtifactEntry.loadPlayerData(uuid: UUID): JsonObject {
    val root = loadData()
    return root[uuid.toString()]?.asJsonObject ?: JsonObject()
}

/**
 * Helper for nullable player identifiers.
 */
fun ArtifactEntry.loadPlayerDataOrEmpty(uuid: UUID?): JsonObject =
    if (uuid == null) JsonObject() else loadPlayerData(uuid)

/**
 * Persists the given player-specific data into this artifact while preserving
 * data for other players.
 */
fun ArtifactEntry.savePlayerData(uuid: UUID, obj: JsonObject) {
    if (artifactId.isBlank()) return
    val root = loadData()
    root.add(uuid.toString(), obj)
    saveData(root)
}

fun ArtifactEntry.loadDefinitionData(id: String): JsonObject {
    val uuid = UUID.nameUUIDFromBytes(id.toByteArray())
    return loadPlayerData(uuid)
}

fun ArtifactEntry.saveDefinitionData(id: String, obj: JsonObject) {
    val uuid = UUID.nameUUIDFromBytes(id.toByteArray())
    savePlayerData(uuid, obj)
}

fun ArtifactEntry.removeDefinitionData(id: String) {
    if (artifactId.isBlank()) return
    val uuid = UUID.nameUUIDFromBytes(id.toByteArray())
    val root = loadData()
    root.remove(uuid.toString())
    saveData(root)
}


