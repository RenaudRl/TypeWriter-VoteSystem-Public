package btc.renaud.votes

/**
 * Represents the available option slots in a poll. Each constant stores
 * the zero-based index that will be persisted for a player's vote.
 *
 * Use [availableOptions] to obtain the subset of options that are valid
 * for a given [VoteDefinitionEntry].
 */
enum class VoteOption(val index: Int) {
    OPTION_1(0),
    OPTION_2(1),
    OPTION_3(2),
    OPTION_4(3),
    OPTION_5(4),
    OPTION_6(5),
    OPTION_7(6),
    OPTION_8(7),
    OPTION_9(8),
    OPTION_10(9);

    companion object {
        /**
         * Returns exactly as many enum values as the definition has options.
         */
        fun availableOptions(definition: VoteDefinitionEntry): List<VoteOption> =
            values().take(definition.options.size)
    }
}
