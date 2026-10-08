package btc.renaud.votes

import com.typewritermc.engine.paper.snippets.snippet

// What the `/tw vote ...` commands answer is a Typewriter snippet: the server owner changes it in the snippets file, nothing is
// fixed in the code. The values below are the defaults a snippet starts from. The replies go through the engine `msg`, which
// puts the Typewriter chat prefix in front and reads MiniMessage; the command errors (entry not found, ...) are plain text.
// The messages of a poll (closed, voted or not) are fields of the vote entries, not snippets.

private const val PATH = "content.vote."

private val usageText by snippet(
    PATH + "usage", "<gray>Usage: /tw vote <definition> <option> [target]</gray>",
    "Answer to /tw vote with no definition. MiniMessage: a tag that is not a known one is shown as written.",
)
private val optionUnavailableText by snippet(
    PATH + "option_unavailable", "<red>Option {option} is not available for {definition}.</red>",
    "Answer when the option number typed does not exist in the poll. {option} is the number typed, {definition} the id of the poll.",
)
private val alreadyVotedText by snippet(
    PATH + "already_voted", "<red>{player} cannot vote in {definition} yet.</red>",
    "Answer when the voter cannot vote again: a first vote is final, or the cooldown of the Vote System Configuration entry is not over. {player} is the voter, {definition} the id of the poll.",
)
private val castOptionFallbackText by snippet(
    PATH + "cast_option_fallback", "option {number}",
    "How an option that has no text is named in the messages below. {number} is its place in the poll (from 1).",
)
private val recordedForVoterText by snippet(
    PATH + "recorded_for_voter", "Your vote for <blue>{definition}</blue> has been recorded as <green>{option}</green>.",
    "Sent to the player whose vote was recorded. {definition} is the id of the poll, {option} the text of the option chosen.",
)
private val recordedByOtherText by snippet(
    PATH + "recorded_by_other", "Recorded vote for <blue>{player}</blue> in {definition}: {option}.",
    "Sent to the staff member who voted for another player (/tw vote <definition> <option> <target>). {player} is the voter, {definition} the id of the poll, {option} the text of the option chosen.",
)
private val recordedForSelfText by snippet(
    PATH + "recorded_for_self", "Vote registered for {definition}: {option}.",
    "Sent to a player who voted for themselves, after the message they already get about their own vote. {definition} is the id of the poll, {option} the text of the option chosen.",
)
private val castFailedText by snippet(
    PATH + "cast_failed", "<red>Unable to register the vote for {player} in {definition}.</red>",
    "Answer when the vote was refused for a reason with no text of its own: no data entry, option out of range, or a closed poll whose closing message is empty. {player} is the voter, {definition} the id of the poll.",
)
private val resetDoneText by snippet(
    PATH + "reset_done", "<green>Votes reset for {definition}.</green>",
    "Answer to /tw vote reset <definition>. {definition} is the id of the poll.",
)
private val noDefinitionsText by snippet(
    PATH + "no_definitions", "There are no active vote definitions.",
    "Answer to /tw vote stats when no poll is loaded.",
)
private val statsLineText by snippet(
    PATH + "stats_line", "Stats for {name} [{definition}]: {options} | total: {total}",
    "One line per poll for /tw vote stats. {name} is the poll display name (its name when that is empty), {definition} its id, {options} the options joined as the two snippets below word them, {total} the votes cast.",
)
private val statsOptionText by snippet(
    PATH + "stats_option", "{number}: {votes} ({label})",
    "One option inside {options} of the stats line. {number} is its place (from 1), {votes} its votes, {label} its text.",
)
private val statsOptionFallbackText by snippet(
    PATH + "stats_option_fallback", "Option {number}",
    "The {label} of an option that has no text. {number} is its place (from 1).",
)
private val statsSeparatorText by snippet(
    PATH + "stats_separator", ", ",
    "What goes between the options inside {options} of the stats line.",
)
private val entryNotFoundText by snippet(
    PATH + "entry_not_found", "Could not find entry {entry}",
    "Command error when the poll typed is neither an id nor a name of a poll. Plain text, no MiniMessage. {entry} is what was typed.",
)
private val entryRejectedText by snippet(
    PATH + "entry_rejected", "Entry did not pass filter",
    "Command error when the entry typed is refused by the command. Plain text, no MiniMessage.",
)

/** The answers of the vote commands, each a snippet filled with its values. */
internal object VoteTexts {

    /** Loads the snippets (they register when this file loads), so the snippets file lists them as soon as the extension starts. */
    fun register() {
        usage()
    }

    fun usage() = usageText

    fun optionUnavailable(option: Int, definition: String) =
        VoteTextTemplate.fill(optionUnavailableText, mapOf("option" to "$option", "definition" to definition))

    fun alreadyVoted(player: String, definition: String) =
        VoteTextTemplate.fill(alreadyVotedText, mapOf("player" to player, "definition" to definition))

    /** How an option that has no text is named when a vote is recorded; [index] is 0-based. */
    fun castOptionFallback(index: Int) =
        VoteTextTemplate.fill(castOptionFallbackText, mapOf("number" to "${index + 1}"))

    fun recordedForVoter(definition: String, option: String) =
        VoteTextTemplate.fill(recordedForVoterText, mapOf("definition" to definition, "option" to option))

    fun recordedByOther(player: String, definition: String, option: String) =
        VoteTextTemplate.fill(recordedByOtherText, mapOf("player" to player, "definition" to definition, "option" to option))

    fun recordedForSelf(definition: String, option: String) =
        VoteTextTemplate.fill(recordedForSelfText, mapOf("definition" to definition, "option" to option))

    fun castFailed(player: String, definition: String) =
        VoteTextTemplate.fill(castFailedText, mapOf("player" to player, "definition" to definition))

    fun resetDone(definition: String) = VoteTextTemplate.fill(resetDoneText, mapOf("definition" to definition))

    fun noDefinitions() = noDefinitionsText

    fun entryNotFound(entry: String) = VoteTextTemplate.fill(entryNotFoundText, mapOf("entry" to entry))

    fun entryRejected() = entryRejectedText

    fun stats(displayName: String, definitionId: String, counts: List<Int>, labels: List<String>, total: Int) =
        VoteTextTemplate.statsLine(
            VoteTextTemplate.StatsWording(statsLineText, statsOptionText, statsOptionFallbackText, statsSeparatorText),
            displayName, definitionId, counts, labels, total,
        )
}
