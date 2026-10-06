package btc.renaud.votes.services

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

/**
 * Pure rules and bookkeeping of one poll, kept apart from the artifact I/O so the cooldown can be
 * tested without a server.
 *
 * A poll is stored as `{ "players": {uuid: option}, "castAt": {uuid: epochMillis}, "options": [counts], "total": n }`.
 * `castAt` was added with the cooldown; polls saved before it simply lack the key.
 */
object VoteTally {
    private const val MILLIS_PER_SECOND = 1_000L

    /**
     * Whether a player who last voted at [previousCastAt] may not vote again.
     *
     * `null` means the player never voted. With no cooldown (`<= 0`) the first vote is final, as
     * before the setting existed; otherwise the player is free again once the cooldown has elapsed.
     */
    fun isBlocked(previousCastAt: Long?, cooldownSeconds: Int, now: Long): Boolean {
        previousCastAt ?: return false
        if (cooldownSeconds <= 0) return true
        return now - previousCastAt < cooldownSeconds * MILLIS_PER_SECOND
    }

    /**
     * When [uuid] last voted, or `null` if they never did.
     *
     * A vote saved before `castAt` existed has no date: it is reported as cast at epoch 0, so it
     * stays final without a cooldown and counts as long expired once a cooldown is configured.
     */
    fun previousCastAt(data: JsonObject, uuid: String): Long? {
        val players = data.getAsJsonObject("players") ?: return null
        if (!players.has(uuid)) return null
        return data.getAsJsonObject("castAt")?.get(uuid)?.asLong ?: 0L
    }

    /**
     * Records one vote for [optionIndex]. Counts are cumulative: a second vote after a cooldown adds
     * to the newly chosen option and keeps the earlier one, like two separate votes would.
     *
     * The caller has already checked [optionIndex] against [optionCount] and [isBlocked].
     */
    fun record(data: JsonObject, uuid: String, optionIndex: Int, optionCount: Int, now: Long) {
        val players = data.getAsJsonObject("players") ?: JsonObject().also { data.add("players", it) }
        val castAt = data.getAsJsonObject("castAt") ?: JsonObject().also { data.add("castAt", it) }
        val options = data.getAsJsonArray("options") ?: JsonArray().also { data.add("options", it) }
        while (options.size() < optionCount) options.add(0)

        options[optionIndex] = JsonPrimitive(options[optionIndex].asInt + 1)
        data.addProperty("total", (data["total"]?.asInt ?: 0) + 1)
        players.addProperty(uuid, optionIndex)
        castAt.addProperty(uuid, now)
    }
}
