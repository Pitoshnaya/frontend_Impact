package pitoshnaya.impact_game

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import pitoshnaya.impact_game.network.GameServer
import pitoshnaya.impact_game.scene.MainMenu
import pitoshnaya.impact_game.scene.SceneController
import java.net.InetSocketAddress

class Launcher : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        GameServer.use(InetSocketAddress (Config.SERVER_ADDRESS, Config.SERVER_PORT))
        SceneController.display = this
        SceneController.set<MainMenu>()
    }
}
