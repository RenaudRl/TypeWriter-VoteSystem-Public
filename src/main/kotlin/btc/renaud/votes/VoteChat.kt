package btc.renaud.votes

import com.typewritermc.engine.paper.extensions.placeholderapi.parsePlaceholders
import com.typewritermc.engine.paper.utils.sendMini
import org.bukkit.entity.Player

/**
 * Sends a message an admin wrote in a vote entry (poll closed, vote refused, voted or not).
 *
 * Placeholders are resolved first, then the text is read as MiniMessage: the colours and styles typed in the editor show up,
 * and so do the ones a placeholder returns (an option text such as `<green>Option A</green>`).
 * `Player.sendMessage(String)` must not be used for these texts: it prints the tags as written. A blank text sends nothing.
 */
internal fun Player.sendPollMessage(text: String) {
    if (text.isBlank()) return
    sendMini(text.parsePlaceholders(this))
}
