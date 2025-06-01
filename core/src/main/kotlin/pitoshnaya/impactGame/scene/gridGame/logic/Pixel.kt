package pitoshnaya.impactGame.scene.gridGame.logic

import com.badlogic.gdx.graphics.Color

internal data class Pixel(val x: Int, val y: Int, var color: String) {
    val id
        get() = "${x}_${y}"

    constructor(x: Int, y: Int, color: Color) : this(x, y, "#${color.toString().substring(0, 6)}")

    val hexColor: Color
        get() = Color.valueOf(color)

    val position: Position
        get() = Position(x, y)
}
