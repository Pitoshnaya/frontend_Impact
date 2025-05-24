package pitoshnaya.impactGame.scene.gridGame

import com.badlogic.gdx.graphics.Color

internal data class Position(val x: Int, val y: Int)

internal data class Pixel(val x: Int, val y: Int, var color: String) {
    val id
        get() = "${x}_${y}"

    constructor(x: Int, y: Int, color: Color) : this(x, y, color.toString())

    val hexColor: Color
        get() = Color.valueOf(color)

    val position: Position
        get() = Position(x, y)
}
