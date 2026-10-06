package btc.renaud.votes.services

import com.google.gson.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VoteTallyTest {
    private val alice = "11111111-1111-1111-1111-111111111111"
    private val bob = "22222222-2222-2222-2222-222222222222"

    private fun counts(data: JsonObject) = data.getAsJsonArray("options").map { it.asInt }

    @Test
    fun `a player who never voted is not blocked whatever the cooldown`() {
        assertFalse(VoteTally.isBlocked(previousCastAt = null, cooldownSeconds = 0, now = 5_000))
        assertFalse(VoteTally.isBlocked(previousCastAt = null, cooldownSeconds = 60, now = 5_000))
    }

    @Test
    fun `without a cooldown the first vote is final`() {
        assertTrue(VoteTally.isBlocked(previousCastAt = 1_000, cooldownSeconds = 0, now = 10_000_000))
    }

    @Test
    fun `with a cooldown the player stays blocked until it has fully elapsed`() {
        val castAt = 10_000L

        assertTrue(VoteTally.isBlocked(castAt, cooldownSeconds = 60, now = castAt + 59_999))
        assertFalse(VoteTally.isBlocked(castAt, cooldownSeconds = 60, now = castAt + 60_000))
    }

    @Test
    fun `recording a vote counts it and remembers when and for what`() {
        val data = JsonObject()

        VoteTally.record(data, alice, optionIndex = 1, optionCount = 3, now = 42_000)

        assertEquals(listOf(0, 1, 0), counts(data))
        assertEquals(1, data["total"].asInt)
        assertEquals(1, data.getAsJsonObject("players")[alice].asInt)
        assertEquals(42_000L, VoteTally.previousCastAt(data, alice))
    }

    @Test
    fun `a second vote after the cooldown adds to the new option and keeps the first`() {
        val data = JsonObject()
        VoteTally.record(data, alice, optionIndex = 0, optionCount = 2, now = 1_000)

        assertFalse(VoteTally.isBlocked(VoteTally.previousCastAt(data, alice), cooldownSeconds = 30, now = 31_000))
        VoteTally.record(data, alice, optionIndex = 1, optionCount = 2, now = 31_000)

        assertEquals(listOf(1, 1), counts(data))
        assertEquals(2, data["total"].asInt)
        assertEquals(1, data.getAsJsonObject("players")[alice].asInt)
        assertEquals(31_000L, VoteTally.previousCastAt(data, alice))
    }

    @Test
    fun `players are tracked separately`() {
        val data = JsonObject()
        VoteTally.record(data, alice, optionIndex = 0, optionCount = 2, now = 1_000)

        assertNull(VoteTally.previousCastAt(data, bob))
    }

    @Test
    fun `a vote saved before dates existed stays final without a cooldown and expired with one`() {
        val legacy = JsonObject().apply {
            add("players", JsonObject().apply { addProperty(alice, 0) })
        }

        val previous = VoteTally.previousCastAt(legacy, alice)

        assertEquals(0L, previous)
        assertTrue(VoteTally.isBlocked(previous, cooldownSeconds = 0, now = 1_000_000))
        assertFalse(VoteTally.isBlocked(previous, cooldownSeconds = 30, now = 1_000_000))
    }

    @Test
    fun `counts of options added after the poll started are padded with zero`() {
        val data = JsonObject()
        VoteTally.record(data, alice, optionIndex = 0, optionCount = 2, now = 1_000)

        VoteTally.record(data, bob, optionIndex = 3, optionCount = 4, now = 2_000)

        assertEquals(listOf(1, 0, 0, 1), counts(data))
    }
}
