# VoteSystem Extension

![Java Version](https://img.shields.io/badge/Java-21-orange)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Target](https://img.shields.io/badge/Target-Paper-blue)
![Typewriter](https://img.shields.io/badge/Typewriter-0.9.0--beta--177-purple)

**VoteSystem Extension** is a poll module for **TypeWriter**, engineered for **BTC Studio** infrastructure. Players pick one option of a vote definition; results are stored in an artifact and exposed through commands and placeholders.

---

## 🚀 Key Features

### 🗳️ Voting Mechanics
- **Vote definitions**: a display name, a list of options, an optional ISO-8601 `endDate` and a `closedMessage` shown once voting is closed.
- **One vote per poll, or a cooldown**: with `cooldownSeconds` at 0 (default) the first vote is final. Above 0, a player can vote again after that many seconds; counts are cumulative.
- **Legacy votes**: votes saved before the cooldown existed are dated at the first contact after the update, so their cooldown starts then.
- **Debug log**: `debug` in the Vote Config logs votes accepted or refused.

### ⌨️ Commands & permissions

| Command | Permission | Description |
|---|---|---|
| `/tw vote` | `typewriter.vote` | Show usage. |
| `/tw vote <definition> <option> [target]` | `typewriter.vote.cast` | Cast a vote (options are numbered from 1). |
| `/tw vote reset <definition>` | `typewriter.vote.reset` | Reset the votes of a poll. |
| `/tw vote stats [definition]` | `typewriter.vote.stats` | Show the results of one or all polls. |

### 🔣 Placeholders
`vote_option_<id>_<n>`, `vote_display_<id>`, `vote_total_<id>`, `vote_stats_<id>[_<n>]` (alias `vote_votes_`), `vote_player_<id>`, `vote_has_voted_<id>` and `vote_closed_<id>` (both `1` or `0`), and `vote_remaining_<id>` (seconds before the end date, `0` without one).

---

## ⚙️ Configuration

VoteSystem Extension configuration is managed via TypeWriter's manifest system. Requires Typewriter `0.9.0-beta-177` on Paper.

| Entry | Id | Role |
|---|---|---|
| Vote Definition | `vote_definition` | A poll: `displayName`, `options`, `endDate`, `closedMessage`, `blockedMessage`, `data` (artifact) |
| Vote Data | `vote_data` | Artifact storing the votes |
| Vote System Configuration | `vote_config` | `cooldownSeconds` and `debug` |
| Vote Action | `vote_action` | Cast a vote on an option (0-based index). Its `triggers` and modifiers run only when the vote is recorded; a refused vote shows the closed or blocked message of the poll and fires `refusedTriggers` |
| Vote Message | `vote_message` | Message depending on whether the player already voted |
| Vote Cast Event | `vote_cast_event` | Event entry with `triggers` |
| Vote Fact | `vote_fact` | Fact entry |

The Vote Cast Event and Vote Fact entries are declared, but this extension does not fire or read them yet.

## 🛠 Building & Deployment

Requires **Java 21**.

```bash
# Clone the repository
git clone https://github.com/RenaudRl/TypeWriter-VoteSystem-Public.git
cd TypeWriter-VoteSystem-Public

# Build the project
./gradlew clean build
```

### Artifact Locations:
- `build/libs/VoteSystem-[Version].jar`

---

## 🤝 Credits & Inspiration
- **[TypeWriter](https://github.com/gabber235/Typewriter)** - The engine this extension is built for.
- **[BTC Studio](https://github.com/RenaudRl)** - Maintenance and specialized optimizations.

---

## 📜 License
Licensed under the **MIT License**.

## Documentation

Full documentation available at [BTC Studio Docs](https://docs.borntocraftstudio.net/extensions/free/vote-system/).

---

## 📜 Licence

**GNU General Public License v3.0 or later** — [LICENSE](LICENSE) — with a
**linking exception** for the Typewriter engine — [LICENSE-EXCEPTION.md](LICENSE-EXCEPTION.md).

| | |
|---|---|
| You may | Run it anywhere, **including on a monetised server**. Study it, modify it, use it as a base, and redistribute it — **even for a fee**. GPLv3 §4 explicitly allows charging for a copy. |
| You must | Publish the complete corresponding source of your version under GPLv3, preserve the copyright notices, and **state that you modified it and when** (§5(a)). |
| You may not | Ship a closed-source or proprietary version, relicense under stricter terms, or strip the attribution and present this work as your own — §8 terminates your rights automatically. |
| Marks | **"Born To Craft"** and **"BTC Studio"** are **not** covered by the GPL. Fork it freely, sell your fork if you like — but **rebrand it**. |

> Reselling this code is legally allowed and practically pointless: whoever buys a
> copy from you receives, under the GPL, the right to redistribute it for free.
> That is the protection — not a clause forbidding sale, which the GPL does not
> permit us to add.

### About Typewriter

This is a **third-party extension**. It uses the public extension API of the
[Typewriter](https://github.com/gabber235/Typewriter) engine by gabber235 and
contains none of its source. Born To Craft Studio is not affiliated with or
endorsed by the Typewriter project.

The engine itself is **not** free software — its licence forbids redistributing
it. **Get it from the Typewriter project, and never redistribute it**, including
inside a fork of this repository.

Full attribution, the statement of modifications required by §5(a), and the
trademark reservation are in **[NOTICE.md](NOTICE.md)**. Read it before
redistributing.

© 2026 Born To Craft Studio.
