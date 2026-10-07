package btc.renaud.votes

/**
 * Fills the `{name}` placeholders of the texts a server owner edits (the snippets of [VoteTexts]).
 *
 * Kept free of the engine so the wording logic runs in plain unit tests.
 */
internal object VoteTextTemplate {
    private val placeholder = Regex("""\{(\w+)}""")

    /**
     * Replaces each `{name}` of [template] with its value from [values] in a single pass: a value that itself contains
     * `{something}` (an option label an admin wrote) is never filled a second time. A placeholder with no value stays as written.
     */
    fun fill(template: String, values: Map<String, String>): String =
        placeholder.replace(template) { match -> values[match.groupValues[1]] ?: match.value }

    /** The wordings the stats line is built from; [VoteTexts] reads them from the snippets. */
    data class StatsWording(
        val line: String,
        val option: String,
        val optionFallback: String,
        val separator: String,
    )

    /**
     * One stats line: [counts] and [labels] are paired by position, a blank label takes the fallback wording
     * (`{number}` is the 1-based place of the option).
     */
    fun statsLine(
        wording: StatsWording,
        displayName: String,
        definitionId: String,
        counts: List<Int>,
        labels: List<String>,
        total: Int,
    ): String {
        val options = counts.mapIndexed { index, count ->
            val number = (index + 1).toString()
            val label = labels.getOrNull(index).orEmpty().ifBlank { fill(wording.optionFallback, mapOf("number" to number)) }
            fill(wording.option, mapOf("number" to number, "votes" to count.toString(), "label" to label))
        }.joinToString(wording.separator)

        return fill(
            wording.line,
            mapOf("name" to displayName, "definition" to definitionId, "options" to options, "total" to total.toString()),
        )
    }
}
