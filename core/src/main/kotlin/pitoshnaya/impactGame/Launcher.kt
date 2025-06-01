package pitoshnaya.impactGame

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import pitoshnaya.impactGame.di.ServiceContainer
import pitoshnaya.impactGame.scene.mainMenu.MainMenu
import pitoshnaya.impactGame.scene.SceneController
import pitoshnaya.network.NetworkClient

class Launcher : KtxGame<KtxScreen>() {
    private val networkClient by lazy { ServiceContainer.get<NetworkClient>() }

    override fun create() {
        KtxAsync.initiate()

        SceneController.display = this
        SceneController.set<MainMenu>()
    }

    override fun dispose() {
        networkClient.disconnect()

        super.dispose()
    }
}
