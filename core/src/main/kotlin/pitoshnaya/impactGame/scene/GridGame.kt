package pitoshnaya.impactGame.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.google.gson.Gson
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import pitoshnaya.impactGame.network.GameServer
import pitoshnaya.impactGame.scene.mainMenu.MainMenu
import kotlin.math.sqrt
import pitoshnaya.impactGame.auth.Manager as AuthManager

val json = Gson()

private data class Pixel(val x: Int, val y: Int, private val color: String) {
    fun getColor(): Color {
        val color = this.color.substring(1)

        return when (color.length) {
            3 -> Color(
                Integer.valueOf("${color[0]}${color[0]}", 16) / 255f,
                Integer.valueOf("${color[1]}${color[1]}", 16) / 255f,
                Integer.valueOf("${color[2]}${color[2]}", 16) / 255f,
                1f
            )

            6 -> Color(
                Integer.valueOf(color.substring(0, 2), 16) / 255f,
                Integer.valueOf(color.substring(2, 4), 16) / 255f,
                Integer.valueOf(color.substring(4, 6), 16) / 255f,
                1f
            )

            8 -> Color(
                Integer.valueOf(color.substring(0, 2), 16) / 255f,
                Integer.valueOf(color.substring(2, 4), 16) / 255f,
                Integer.valueOf(color.substring(4, 6), 16) / 255f,
                Integer.valueOf(color.substring(6, 8), 16) / 255f
            )

            else -> throw IllegalArgumentException("Invalid hex color: $color")
        }
    }
}

private object GameAPI {
    const val CANVAS = "/grid/canvas"

    fun getGrid(success: (Array<Pixel>) -> Unit) {
        KtxAsync.launch {
            GameServer.consume(CANVAS)
                .onSuccess {
                    val grid = json.fromJson(it, Array<Pixel>::class.java)
                    success(grid)
                }.onFailure {
                    println("whoops")
                }
        }
    }
}

class GridGame : Scene() {
    private val content: Table = Table()

    override fun load(): Boolean {
        val player = AuthManager.getCurrentUser()
        if (player == null || !player.isAuthenticated()) {
            SceneController.set<MainMenu>()

            return false;
        }
        GameServer.connect(player)

        loadBoard()

        wrapper.addActor(content)

        return true
    }

    private fun loadBoard() {
        content.clear()
        content.setFillParent(true)
        content.add(Label("Loading canvas", theme)).width(300f).pad(20f)
        content.row()
        refresh()
    }

    private fun refresh() {
        GameAPI.getGrid { grid ->
            val perRow = sqrt(grid.size.toDouble()).toInt()
            check(perRow * perRow == grid.size) { "Grid must be a square" }

            // 0.90 - это какую часть экрана мы хотим занять конечной отрисовкой
            val pixelSize = ((getScreenHeight().toFloat() * 0.90) / perRow).toFloat()
            content.clear()
            grid.forEachIndexed { index, cell ->
                if (index % perRow == 0) {
                    content.row()
                }

                content
                    .add(createPixel(cell.getColor()))
                    .width(pixelSize)
                    .height(pixelSize)
            }
        }
    }

    private fun createPixel(background: Color): Button {
        val button = Button(Button.ButtonStyle().apply {
            up = createColoredBackground(background)
        })

        return button
    }

    private fun createColoredBackground(color: Color): Drawable {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
            setColor(color)
            fill()
        }
        val texture = Texture(pixmap)
        pixmap.dispose()

        return TextureRegionDrawable(TextureRegion(texture))
    }
}
