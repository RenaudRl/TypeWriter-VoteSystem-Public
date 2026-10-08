package btc.renaud.votes.services

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VotePolicyTest {
    private val openEnd = ""
    private val endAt = Instant.parse("2026-01-01T00:00:00Z").toEpochMilli()
    private val midway = Instant.parse("2025-06-01T00:00:00Z").toEpochMilli()

    private fun decide(
        optionIndex: Int = 0,
        optionCount: Int = 3,
        endDate: String = openEnd,
        previousCastAt: Long? = null,
        cooldownSeconds: Int = 0,
        now: Long = midway,
    ) = VotePolicy.decide(optionIndex, optionCount, endDate, previousCastAt, cooldownSeconds, now)

    @Test
    fun `a first vote on an open poll is accepted`() {
        assertEquals(VoteOutcome.ACCEPTED, decide())
    }

    @Test
    fun `an option outside the poll is refused on both sides`() {
        assertEquals(VoteOutcome.OPTION_UNAVAILABLE, decide(optionIndex = -1))
        assertEquals(VoteOutcome.OPTION_UNAVAILABLE, decide(optionIndex = 3, optionCount = 3))
    }

    @Test
    fun `a vote at or after the end date is refused as closed`() {
        assertEquals(VoteOutcome.ACCEPTED, decide(endDate = "2026-01-01T00:00:00Z", now = endAt - 1))
        assertEquals(VoteOutcome.CLOSED, decide(endDate = "2026-01-01T00:00:00Z", now = endAt))
        assertEquals(VoteOutcome.CLOSED, decide(endDate = "2020-01-01T00:00:00Z"))
    }

    @Test
    fun `a closed poll is reported as closed even to a player who is also blocked`() {
        val outcome = decide(endDate = "2020-01-01T00:00:00Z", previousCastAt = midway - 1_000, cooldownSeconds = 60)

        assertEquals(VoteOutcome.CLOSED, outcome)
    }

    @Test
    fun `an end date that cannot be read leaves the poll open`() {
        assertEquals(VoteOutcome.ACCEPTED, decide(endDate = "not a date"))
        assertFalse(VotePolicy.isClosed("not a date", now = Long.MAX_VALUE))
    }

    @Test
    fun `without a cooldown the second vote is refused as blocked`() {
        assertEquals(VoteOutcome.BLOCKED, decide(previousCastAt = 1_000, cooldownSeconds = 0))
    }

    @Test
    fun `with a 10 second cooldown a vote 3 seconds later is blocked and one 10 seconds later is accepted`() {
        val firstVoteAt = 1_000_000L

        assertEquals(VoteOutcome.BLOCKED, decide(previousCastAt = firstVoteAt, cooldownSeconds = 10, now = firstVoteAt + 3_000))
        assertEquals(VoteOutcome.ACCEPTED, decide(previousCastAt = firstVoteAt, cooldownSeconds = 10, now = firstVoteAt + 10_000))
    }

    @Test
    fun `a refusal is a refusal and only acceptance is accepted`() {
        assertTrue(VoteOutcome.ACCEPTED.isAccepted)
        VoteOutcome.entries.filter { it != VoteOutcome.ACCEPTED }.forEach { assertFalse(it.isAccepted, "$it") }
    }

    @Test
    fun `remaining seconds count down to zero and stay there`() {
        assertEquals(90L, VotePolicy.remainingSeconds("2026-01-01T00:00:00Z", now = endAt - 90_999))
        assertEquals(0L, VotePolicy.remainingSeconds("2026-01-01T00:00:00Z", now = endAt))
        assertEquals(0L, VotePolicy.remainingSeconds("2026-01-01T00:00:00Z", now = endAt + 5_000))
    }

    @Test
    fun `a poll without an end date has nothing remaining`() {
        assertEquals(0L, VotePolicy.remainingSeconds("", now = 1_000))
        assertFalse(VotePolicy.isClosed("", now = Long.MAX_VALUE))
    }
}
