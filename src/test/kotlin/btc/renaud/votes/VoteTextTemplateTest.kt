package btc.renaud.votes

import kotlin.test.Test
import kotlin.test.assertEquals

class VoteTextTemplateTest {
    private val defaultWording = VoteTextTemplate.StatsWording(
        line = "Stats for {name} [{definition}]: {options} | total: {total}",
        option = "{number}: {votes} ({label})",
        optionFallback = "Option {number}",
        separator = ", ",
    )

    @Test
    fun `every placeholder is replaced, also when it appears twice`() {
        val filled = VoteTextTemplate.fill("{player} voted in {definition}, {player}!", mapOf("player" to "Alice", "definition" to "poll"))

        assertEquals("Alice voted in poll, Alice!", filled)
    }

    @Test
    fun `a placeholder without a value stays as written`() {
        assertEquals("Option {option} of poll", VoteTextTemplate.fill("Option {option} of {definition}", mapOf("definition" to "poll")))
    }

    @Test
    fun `a value that looks like a placeholder is not filled a second time`() {
        val filled = VoteTextTemplate.fill("{option} then {player}", mapOf("option" to "{player}", "player" to "Alice"))

        assertEquals("{player} then Alice", filled)
    }

    @Test
    fun `a text without placeholders comes back unchanged, MiniMessage tags included`() {
        val text = "Your vote for <blue>poll</blue> is in. {not a name}"

        assertEquals(text, VoteTextTemplate.fill(text, mapOf("definition" to "poll")))
    }

    @Test
    fun `the default stats line reads as the command always printed it`() {
        val line = VoteTextTemplate.statsLine(defaultWording, "Best color", "color_poll", listOf(3, 1), listOf("Red", "Blue"), 4)

        assertEquals("Stats for Best color [color_poll]: 1: 3 (Red), 2: 1 (Blue) | total: 4", line)
    }

    @Test
    fun `an option without text takes the fallback wording with its place`() {
        val line = VoteTextTemplate.statsLine(defaultWording, "Poll", "p", listOf(0, 2), listOf("", "  "), 2)

        assertEquals("Stats for Poll [p]: 1: 0 (Option 1), 2: 2 (Option 2) | total: 2", line)
    }

    @Test
    fun `a poll without options still prints its line`() {
        assertEquals("Stats for Poll [p]:  | total: 0", VoteTextTemplate.statsLine(defaultWording, "Poll", "p", emptyList(), emptyList(), 0))
    }

    @Test
    fun `the stats wording can be reshaped from the snippets`() {
        val wording = VoteTextTemplate.StatsWording("{name}: {options} ({total})", "{label}={votes}", "#{number}", " / ")

        val line = VoteTextTemplate.statsLine(wording, "Poll", "p", listOf(5, 6), listOf("Yes", ""), 11)

        assertEquals("Poll: Yes=5 / #2=6 (11)", line)
    }

    @Test
    fun `an option label that holds a placeholder is printed as typed`() {
        val line = VoteTextTemplate.statsLine(defaultWording, "Poll", "p", listOf(1), listOf("{total}"), 1)

        assertEquals("Stats for Poll [p]: 1: 1 ({total}) | total: 1", line)
    }
}
