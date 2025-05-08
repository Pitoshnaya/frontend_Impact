package pitoshnaya.impactGame

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import pitoshnaya.impactGame.network.GameServer
import pitoshnaya.impactGame.scene.mainMenu.MainMenu
import pitoshnaya.impactGame.scene.SceneController

class Launcher : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        SceneController.display = this
        SceneController.set<MainMenu>()
    }

    override fun dispose() {
        GameServer.disconnect()
        super.dispose()
    }
}
