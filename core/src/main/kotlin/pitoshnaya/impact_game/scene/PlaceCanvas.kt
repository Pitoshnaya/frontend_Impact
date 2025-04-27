/*package pitoshnaya.impact_game.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.google.gson.Gson
import kotlinx.coroutines.launch
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import ktx.async.httpRequest
import kotlin.math.min

data class By(val name: String)
data class GridEntry(val x: Int, val y: Int, val color: String, val by: By)

class PlaceCanvas : KtxScreen {
    private val CELL_OFFSET = 5
    private val size = min(Gdx.graphics.width.toFloat()/5,  Gdx.graphics.height.toFloat()/5)
    private val shapeRenderer = ShapeRenderer()
    private val gridEntries = mutableListOf<GridEntry>()

    override fun show() {
        // Fetch grid data
        KtxAsync.launch {
            val response = httpRequest("http://127.0.0.1:8081/gridmock.json")
            if (response.statusCode == 200) {
                gridEntries.clear()
                gridEntries.addAll(parseJsonList(response.getContentAsString()))
            }
        }
    }

    override fun render(delta: Float) {
        super.render(delta)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        gridEntries.forEach { entry ->
            val x = entry.x.toFloat()
            val y = entry.y.toFloat()
            val color = Color.valueOf(entry.color)
            shapeRenderer.color = color

            shapeRenderer.rect(x * size + CELL_OFFSET, y * size + CELL_OFFSET, size - 5, size - 5)
        }
        shapeRenderer.end()
    }

    override fun hide() {
        shapeRenderer.dispose()
    }

    private fun parseJsonList(json: String): Array<GridEntry> {
        // assuming you have a library to parse JSON to objects (e.g., Gson or kotlinx.serialization)
        return Gson().fromJson(json, Array<GridEntry>::class.java)
    }
}
*/