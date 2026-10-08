package btc.renaud.votes.services

import java.time.Instant

/** What became of one attempt to vote. Only [ACCEPTED] changes the stored poll. */
enum class VoteOutcome(val logReason: String) {
    ACCEPTED("accepted"),
    OPTION_UNAVAILABLE("option does not exist"),
    NO_DATA("no data entry"),
    CLOSED("poll closed"),
    BLOCKED("still blocked");

    val isAccepted: Boolean get() = this == ACCEPTED
}

/**
 * Who may vote and when, free of the engine and of the artifact I/O so every refusal can be tested without a server.
 * The cooldown arithmetic itself stays in [VoteTally.isBlocked].
 */
object VotePolicy {
    private const val MILLIS_PER_SECOND = 1_000L

    /**
     * Decides one attempt. The checks run in a fixed order, so the reason given is the first one that stops the vote:
     * an option that does not exist, a poll past its end date, a player who must still wait.
     *
     * [previousCastAt] is when the player last voted in this poll (see [VoteTally.previousCastAt]), `null` if never.
     * An [endDate] that is blank or cannot be read leaves the poll open.
     */
    fun decide(
        optionIndex: Int,
        optionCount: Int,
        endDate: String,
        previousCastAt: Long?,
        cooldownSeconds: Int,
        now: Long,
    ): VoteOutcome {
        if (optionIndex !in 0 until optionCount) return VoteOutcome.OPTION_UNAVAILABLE
        if (isClosed(endDate, now)) return VoteOutcome.CLOSED
        if (VoteTally.isBlocked(previousCastAt, cooldownSeconds, now)) return VoteOutcome.BLOCKED
        return VoteOutcome.ACCEPTED
    }

    /** True once [now] has reached the end date; a poll without a readable end date never closes. */
    fun isClosed(endDate: String, now: Long): Boolean {
        val end = endMillis(endDate) ?: return false
        return now >= end
    }

    /** Whole seconds left before the end date, 0 when there is none or it has passed. */
    fun remainingSeconds(endDate: String, now: Long): Long {
        val end = endMillis(endDate) ?: return 0L
        return (end - now).coerceAtLeast(0L) / MILLIS_PER_SECOND
    }

    private fun endMillis(endDate: String): Long? {
        if (endDate.isBlank()) return null
        return runCatching { Instant.parse(endDate).toEpochMilli() }.getOrNull()
    }
}
