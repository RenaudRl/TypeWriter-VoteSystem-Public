package btc.renaud.votes

import com.typewritermc.core.extension.Initializable
import com.typewritermc.core.extension.annotations.Singleton
import org.koin.java.KoinJavaComponent.get
import btc.renaud.votes.services.VoteService

@Singleton
object VoteInitializer : Initializable {
    override suspend fun initialize() {
        VoteTexts.register()
        get<VoteService>(VoteService::class.java).initialize()
    }

    override suspend fun shutdown() {
        get<VoteService>(VoteService::class.java).shutdown()
    }
}
