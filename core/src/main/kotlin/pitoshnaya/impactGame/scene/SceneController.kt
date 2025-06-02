package pitoshnaya.impactGame.scene

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import pitoshnaya.impactGame.di.ServiceContainer
import kotlin.time.Duration

object SceneController {
    lateinit var display: KtxGame<KtxScreen>

    inline fun <reified T: KtxScreen> set(afterDelay: Duration = Duration.ZERO) {
        KtxAsync.launch {
            if (afterDelay != Duration.ZERO) {
                delay(afterDelay)
            }
            if (!display.containsScreen<T>()) {
                display.addScreen(ServiceContainer.get(T::class))
            }
            display.setScreen<T>()
        }
    }
}
