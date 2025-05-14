package pitoshnaya.impactGame.scene.gridGame

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.google.gson.Gson
import kotlinx.coroutines.delay
import ktx.actors.onClick
import pitoshnaya.impactGame.auth.AuthServer
import pitoshnaya.impactGame.network.ServerEvent
import pitoshnaya.impactGame.scene.Scene
import pitoshnaya.impactGame.scene.SceneController
import pitoshnaya.impactGame.scene.mainMenu.MainMenu
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class GridGame : Scene() {
    private val json = Gson()

    private val server = GridGameServer()
    private lateinit var grid: Map<Position, Button>
    private lateinit var size: Dimension
    private var pixelColor: Color = Color.ORANGE

    override fun load(): Boolean {
        if (AuthServer.getCurrentUser() == null) {
            SceneController.set<MainMenu>()

            return false
        }

        loadBoard()

        server.onEvent { this.handleEvent(it) }
        server.start()

        return true
    }

    override fun dispose() {
        server.stop()

        super.dispose()
    }

    override fun hide() {
        server.stop()
        super.hide()
    }

    override fun show() {
        super.show()

        server.start()
    }

    override fun onResize(newWidth: Int, newHeight: Int) {
        super.onResize(newWidth, newHeight)

        if (!this@GridGame::grid.isInitialized) {
            return
        }

        val pixelSize = ((min(newWidth, newHeight) / size.height) * 0.9).toFloat()
        val offsetX = (newWidth - (size.width * pixelSize)) / 2
        val offsetY = (newHeight - (size.height * pixelSize)) / 2
        grid.forEach {
            it.value.setPosition(
                pixelSize * (it.key.x - 1) + offsetX,
                pixelSize * (it.key.y - 1) + offsetY
            )
            it.value.setSize(pixelSize, pixelSize)
        }
    }

    private fun loadBoard() {
        wrapper.clear()
        wrapper.addActor(Label("Loading canvas", theme))
    }

    private fun fullRedraw(it: Grid) {
        size = it.dimension
        // 0.9 - это какую часть экрана мы хотим занять конечной отрисовкой
        val pixelSize = ((min(getScreenHeight(), getScreenWidth()) / size.height) * 0.9).toFloat()
        val offsetX = (getScreenWidth() - (size.width * pixelSize)) / 2
        val offsetY = (getScreenHeight() - (size.height * pixelSize)) / 2
        // TODO Проверить не остаются ли предыдущие объекты висеть в памяти при переопределении грида
        grid = it.associate { pixel ->
            val pixelButton = createPixelButton(pixel)
            pixelButton.setPosition(
                pixelSize * (pixel.x - 1) + offsetX,
                pixelSize * (pixel.y - 1) + offsetY
            )
            pixelButton.setSize(pixelSize, pixelSize)

            return@associate pixel.position to pixelButton
        }
        wrapper.clear()
        grid.forEach { _, btn -> wrapper.addActor(btn) }
    }

    // TODO заворачивать в доменные структуры. Пусть геймсервер поставляет сразу Grid и т.п.
    private fun handleEvent(event: ServerEvent) {
        when (event.name) {
            GridEvent.INIT -> {
                val gridModel = Grid(json.fromJson(event.payload, Array<Pixel>::class.java).asList())
                fullRedraw(gridModel)
            }

            GridEvent.UPDATED -> {
                val gridModel = Grid(json.fromJson(event.payload, Array<Pixel>::class.java).asList())
                gridModel.forEach { pixel ->
                    grid[pixel.position]!!.style.up = createColoredBackground(pixel.hexColor)
                }
            }

            GridEvent.DISCONNECTED -> {
                server.stop()
                wrapper.clear()
                wrapper.addActor(Label("Disconnected", theme))
                // If auth is still there, this will end up with infinite loop trying to reconnect to server
                SceneController.set<MainMenu>(5.seconds)
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
