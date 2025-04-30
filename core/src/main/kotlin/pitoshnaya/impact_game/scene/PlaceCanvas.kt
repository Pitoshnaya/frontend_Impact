package pitoshnaya.impact_game.scene

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import pitoshnaya.impact_game.auth.Manager as AuthManager

// TODO просто заглушка. Будет сделано в следующих задачах
class PlaceCanvas: Scene() {
    override fun load() {
        val player = AuthManager.getCurrentUser()
        if (player == null || !player.isAuthenticated()) {
            SceneController.set<MainMenu>()

            return;
        }

        val table = Table()
        table.setFillParent(true)
        table.add(Label("You have joined the game", theme)).width(300f).pad(20f)
        table.row()

        wrapper.addActor(table)
    }
}