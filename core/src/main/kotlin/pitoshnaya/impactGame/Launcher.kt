package pitoshnaya.impactGame

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import pitoshnaya.impactGame.network.GameServer
import pitoshnaya.impactGame.scene.MainMenu
import pitoshnaya.impactGame.scene.SceneController
import java.net.InetSocketAddress

class Launcher : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        GameServer.use(InetSocketAddress (Config.SERVER_ADDRESS, Config.SERVER_PORT))
        SceneController.display = this
        SceneController.set<MainMenu>()
    }
}
