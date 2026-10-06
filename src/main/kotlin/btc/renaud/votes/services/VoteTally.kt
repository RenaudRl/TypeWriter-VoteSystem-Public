package btc.renaud.votes.services

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

/**
 * Pure rules and bookkeeping of one poll, kept apart from the artifact I/O so the cooldown can be
 * tested without a server.
 *
 * A poll is stored as `{ "players": {uuid: option}, "castAt": {uuid: epochMillis}, "options": [counts], "total": n }`.
 * `castAt` was added with the cooldown; polls saved before it lack the key and are stamped by [stampLegacy].
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
     * A vote saved before `castAt` existed has no date. [stampLegacy] gives it one at migration, so
     * this fallback only covers data that was not stamped yet: it reports the vote as cast "in the
     * future", which keeps the player blocked rather than granting a free revote.
     */
    fun previousCastAt(data: JsonObject, uuid: String): Long? {
        val players = data.getAsJsonObject("players") ?: return null
        if (!players.has(uuid)) return null
        return data.getAsJsonObject("castAt")?.get(uuid)?.asLong ?: Long.MAX_VALUE
    }

    /**
     * Migration of polls saved before `castAt` existed: every voter without a date is stamped [now],
     * so their cooldown starts at the first contact after the update instead of being expired.
     * Returns true when [data] changed and must be saved.
     */
    fun stampLegacy(data: JsonObject, now: Long): Boolean {
        val players = data.getAsJsonObject("players") ?: return false
        val castAt = data.getAsJsonObject("castAt") ?: JsonObject()
        val undated = players.keySet().filterNot { castAt.has(it) }
        if (undated.isEmpty()) return false

        undated.forEach { castAt.addProperty(it, now) }
        data.add("castAt", castAt)
        return true
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
