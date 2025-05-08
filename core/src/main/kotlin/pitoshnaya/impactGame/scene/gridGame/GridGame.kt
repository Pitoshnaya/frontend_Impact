package pitoshnaya.impactGame.scene.gridGame

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.async.KtxAsync
import pitoshnaya.impactGame.network.GameServer
import pitoshnaya.impactGame.scene.Scene
import pitoshnaya.impactGame.scene.SceneController
import pitoshnaya.impactGame.scene.mainMenu.MainMenu

class GridGame : Scene() {
    private val api = GameAPI()
    private lateinit var grid: Map<Position, Button>
    private var pixelColor: Color = Color.ORANGE

    private var refresher: Job? = null

    override fun load(): Boolean {
        if (!GameServer.connect()) {
            SceneController.set<MainMenu>()

            return false
        }

        loadBoard()

        return true
    }

    override fun dispose() {
        refresher?.cancel()

        super.dispose()
    }

    override fun hide() {
        refresher?.cancel()
        super.hide()
    }

    override fun show() {
        super.show()

        enableSync()
    }

    private fun loadBoard() {
        wrapper.clear()
        wrapper.addActor(Label("Loading canvas", theme))
        refresh(true)

        enableSync()
    }

    private fun enableSync() {
        refresher?.cancel()
        refresher = KtxAsync.launch {
            while (true) {
                delay(2000)
                if (this@GridGame::grid.isInitialized) {
                    refresh(false)
                }
            }
        }
    }

    private fun refresh(full: Boolean = false) {
        api.getGrid {
            // 0.9 - это какую часть экрана мы хотим занять конечной отрисовкой
            val pixelSize = ((getScreenHeight() / it.dimension.height) * 0.9).toFloat()

            // TODO распилить на методы/стратегии
            if (full) {
                grid = it.associate { pixel ->
                    val pixelButton = createPixelButton(pixel)
                    pixelButton.setPosition(pixelSize * (pixel.x - 1), pixelSize * (pixel.y - 1))
                    pixelButton.setSize(pixelSize, pixelSize)

                    return@associate pixel.position to pixelButton
                }
                wrapper.clear()
                grid.forEach { _, btn -> wrapper.addActor(btn) }
            } else {
                it.forEach { pixel ->
                    grid[pixel.position]!!.style.up = createColoredBackground(pixel.hexColor)
                }
            }
        }
    }

    private fun createPixelButton(pixel: Pixel): Button {
        val button = Button(Button.ButtonStyle())
        button.apply {
            style.up = createColoredBackground(pixel.hexColor)
            onClick {
                style.up = createColoredBackground(pixelColor)
            }
        }

        return button
    }

    private fun createColoredBackground(color: Color): Drawable {
        return TextureRegionDrawable(TextureRegion(PixelTexture.forColor(color)))
    }
}
