package pitoshnaya.impactGame.scene.gridGame

import com.badlogic.gdx.graphics.Color

internal data class Position(val x: Int, val y: Int)

internal data class Pixel(val x: Int, val y: Int, val color: String) {
    val hexColor: Color
        get() = Color.valueOf(color)

    val position: Position
        get() = Position(x, y)

    companion object {
        fun new(x: Int, y: Int, color: Color): Pixel {
            return Pixel(x, y, color.toString())
        }
    }
}
