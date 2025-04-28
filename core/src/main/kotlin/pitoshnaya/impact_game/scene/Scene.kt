package pitoshnaya.impact_game.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.app.KtxScreen
import pitoshnaya.impact_game.Config
import pitoshnaya.impact_game.asset.Theme

abstract class Scene(
    protected val theme: Skin = Theme.default(),
    protected val wrapper: Stage = Stage(ScreenViewport()),
) : KtxScreen {
    private var isVisible = false

    private var isLoaded = false

    abstract fun load()

    override fun show() {
        if (!isLoaded) {
            load()
            isLoaded = true
        }

        isVisible = true
        wrapper.isDebugAll = Config.DEBUG_MODE
        Gdx.input.inputProcessor = wrapper
    }

    override fun hide() {
        isVisible = false
        Gdx.input.inputProcessor = null
    }

    final override fun render(delta: Float) {
        if (!isVisible) {
            return
        }

        wrapper.act(delta)
        wrapper.draw()
    }

    final override fun resize(width: Int, height: Int) {
        if (!isVisible) {
            return
        }

        wrapper.viewport.update(width, height, true)
    }

    override fun dispose() {
        wrapper.dispose()
    }
}