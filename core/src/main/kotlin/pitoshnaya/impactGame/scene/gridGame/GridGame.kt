package pitoshnaya.impactGame.scene.gridGame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import pitoshnaya.impactGame.auth.AuthServer
import pitoshnaya.network.ServerEvent
import pitoshnaya.impactGame.ui.ColorPicker
import pitoshnaya.impactGame.scene.Scene
import pitoshnaya.impactGame.scene.SceneController
import pitoshnaya.impactGame.scene.gridGame.logic.Dimension
import pitoshnaya.impactGame.scene.gridGame.logic.Disconnected
import pitoshnaya.impactGame.scene.gridGame.logic.Grid
import pitoshnaya.impactGame.scene.gridGame.logic.GridGameServer
import pitoshnaya.impactGame.scene.gridGame.logic.GridLoaded
import pitoshnaya.impactGame.scene.gridGame.logic.GridUpdated
import pitoshnaya.impactGame.scene.gridGame.logic.Pixel
import pitoshnaya.impactGame.scene.gridGame.logic.PixelColorChanged
import pitoshnaya.impactGame.scene.gridGame.logic.PixelDraw
import pitoshnaya.impactGame.scene.gridGame.logic.Position
import pitoshnaya.impactGame.scene.mainMenu.MainMenu
import kotlin.math.min
import kotlin.time.Duration.Companion.seconds

class GridGame(private val server: GridGameServer) : Scene() {
    private lateinit var gridModel: Grid
    private lateinit var grid: Map<Position, Button>
    private lateinit var size: Dimension
    private var pixelColor: Color = Color.LIGHT_GRAY

    private lateinit var colorPicker: ColorPicker

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
                (pixelSize * it.key.x) + offsetX,
                (pixelSize * it.key.y) + offsetY
            )
            it.value.setSize(pixelSize, pixelSize)
        }
    }

    private fun loadBoard() {
        wrapper.clear()
        wrapper.addActor(Label("Loading canvas", theme))
    }

    private fun fullRedraw() {
        size = gridModel.dimension
        // screenPart - это какую часть экрана мы хотим занять конечной отрисовкой. от 0(0%) до 1(100%)
        val screenPart = 0.7
        val pixelSize = ((min(getScreenHeight(), getScreenWidth()) / size.height) * screenPart).toFloat()
        val offsetX = (getScreenWidth() - (size.width * pixelSize)) / 2
        val offsetY = (getScreenHeight() - (size.height * pixelSize)) / 2 + 50
        // TODO Проверить не остаются ли предыдущие объекты висеть в памяти при переопределении грида
        grid = gridModel.associate { pixel ->
            val pixelButton = createPixelButton(pixel)
            pixelButton.setPosition(
                (pixelSize * pixel.x) + offsetX,
                (pixelSize * pixel.y) + offsetY
            )
            pixelButton.setSize(pixelSize, pixelSize)

            return@associate pixel.position to pixelButton
        }
        wrapper.clear()
        grid.forEach { _, btn -> wrapper.addActor(btn) }

        colorPicker = ColorPicker({ pixelColor = it }, theme)
        colorPicker.changeColor(pixelColor)
        colorPicker.setPosition(0f, 0f)
        wrapper.addActor(colorPicker)
    }

    private fun handleEvent(event: ServerEvent) {
        when (event) {
            is GridLoaded -> {
                gridModel = event.grid
                fullRedraw()
            }

            is GridUpdated -> {
                gridModel.diff(event.grid).forEach { pixel ->
                    gridModel.draw(pixel)
                    grid[pixel.position]!!.style.up = createColoredBackground(pixel.hexColor)
                }
            }

            is PixelColorChanged -> {
                gridModel.draw(event.pixel)
                grid[event.pixel.position]!!.style.up = createColoredBackground(event.pixel.hexColor)
            }

            is Disconnected -> {
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
            addListener(object : InputListener() {
                override fun enter(
                    event: InputEvent?,
                    x: Float,
                    y: Float,
                    pointer: Int,
                    fromActor: Actor?
                ) {
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                        if (isPickingColor()) {
                            colorPicker.changeColor(pixel.hexColor)

                            return
                        }

                        if (pixelColor != pixel.hexColor) {
                            server.send(PixelDraw(pixel.x, pixel.y, pixelColor))
                        }
                    }
                }
            })
        }

        return button
    }

    private fun createColoredBackground(color: Color): Drawable {
        return TextureRegionDrawable(TextureRegion(PixelTexture.forColor(color)))
    }

    private fun isPickingColor(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)
    }
}
