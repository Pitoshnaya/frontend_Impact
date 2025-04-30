package pitoshnaya.impact_game.scene

import ktx.app.KtxGame
import ktx.app.KtxScreen

object SceneController {
    lateinit var display: KtxGame<KtxScreen>

    inline fun <reified T: KtxScreen> set() {
        if (!display.containsScreen<T>()) {
            display.addScreen(T::class.java.getDeclaredConstructor().newInstance())
        }
        display.setScreen<T>()
    }
}