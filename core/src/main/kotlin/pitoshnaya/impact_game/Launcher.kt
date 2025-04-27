package pitoshnaya.impact_game

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import pitoshnaya.impact_game.scene.MainMenu

class Launcher : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()
        addScreen(MainMenu())
        setScreen<MainMenu>()
    }
}
