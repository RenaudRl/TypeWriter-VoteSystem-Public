# Typewriter-VoteSystemExtension
Add Vote System to TypeWriter

## Placeholders

The extension exposes several placeholders via PlaceholderAPI. Use the `id` of the corresponding
`vote_definition` entry as the poll identifier in each placeholder. Index values are
**1-based**:

- `%typewriter_vote_display_<pollId>%` – display name of the poll `pollId`.
- `%typewriter_vote_option_<pollId>_<index>%` – text of the option at `index` in the poll `pollId`.
- `%typewriter_vote_stats_<pollId>_<index>%` – number of votes for the option at `index` in the poll `pollId`.
- `%typewriter_vote_stats_<pollId>%` – comma-separated statistics for all options in the poll `pollId`.
- `%typewriter_vote_player_<pollId>%` – option chosen by the requesting player in poll `pollId`.
- `%typewriter_vote_votes_<pollId>_<index>%` – legacy alias for `vote_stats` with an index.
- `%typewriter_vote_total_<pollId>%` – total number of votes cast in the poll `pollId`.

## Commands

`/vote reset <pollId>` – clears all votes for the given poll.

`/vote stats <pollId>` – prints vote counts for the given poll.

`/vote <pollId> <option> [target]` – vote for the given poll.

`/vote stats` – prints vote counts for all polls.

## Vote Definition

Polls are defined using the `vote_definition` entry. The poll id is the entry `id` and the
`endDate` field expects an ISO-8601 formatted date and time such as `2024-05-01T12:00:00Z`.
