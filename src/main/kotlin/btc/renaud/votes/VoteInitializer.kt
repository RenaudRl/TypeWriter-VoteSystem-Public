package btc.renaud.votes

import com.typewritermc.core.extension.Initializable
import com.typewritermc.core.extension.annotations.Singleton

@Singleton
object VoteInitializer : Initializable {
    override suspend fun initialize() {
        // Nothing to initialize yet
    }

    override suspend fun shutdown() {
        // Nothing to shutdown
    }
}
