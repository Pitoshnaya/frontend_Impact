package pitoshnaya.impactGame.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.app.KtxScreen
import pitoshnaya.impactGame.Config
import pitoshnaya.impactGame.asset.Theme

abstract class Scene(
    protected val theme: Skin = Theme.default(),
    protected val wrapper: Stage = Stage(ScreenViewport()),
) : KtxScreen {
    private var isVisible = false

    private var isLoaded = false

    /**
     *  Сцена должна возвращать true при успешном завершении загрузки и false при неудачном
     *  Таким образом мы предотвращаем всевозможные ошибки, которые возникают при переходах между сценами.
     *  Например, когда сцена загрузки игры не находит сохранение и пытается вернуться в главное меню
     */
    protected abstract fun load(): Boolean

    override fun show() {
        if (!isLoaded) {
            isLoaded = load()
        }

        if (!isLoaded) {
            return
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

    protected fun getScreenWidth(): Int
    {
        return wrapper.viewport.screenWidth
    }

    protected fun getScreenHeight(): Int
    {
        return wrapper.viewport.screenHeight
    }
}
