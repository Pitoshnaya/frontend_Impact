package pitoshnaya.impactGame.scene.gridGame

import com.badlogic.gdx.graphics.Color
import kotlin.math.sqrt

class Dimension(val width: Int, val height: Int)

internal class Grid(private val entries: List<Pixel>): Iterable<Pixel> {
    val dimension: Dimension
    private val pixels: MutableMap<String, Pixel> = mutableMapOf()

    init {
        val size = sqrt(entries.size.toDouble()).toInt()
        require(size * size == entries.size) { "Grid must be a square" }
        dimension = Dimension(size, size)

        entries.forEach {
            pixels["${it.x}_${it.y}"] = it
        }
    }

    fun tagPixel(x: Int, y: Int, color: Color) {
        pixels["${x}_${y}"] = Pixel.new(x, y, color)
    }

    override fun iterator(): Iterator<Pixel> {
        return object : Iterator<Pixel> {
            private var pointer = 0

            override fun next(): Pixel {
                return entries[pointer++]
            }

            override fun hasNext(): Boolean {
                return entries.getOrNull(pointer) != null
            }
        }
    }
}
