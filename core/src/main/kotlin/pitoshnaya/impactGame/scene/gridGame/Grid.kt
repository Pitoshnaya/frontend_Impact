package pitoshnaya.impactGame.scene.gridGame

import com.badlogic.gdx.graphics.Color
import kotlin.math.sqrt

internal class Grid(private val entries: List<Pixel>): Iterable<Pixel> {
    val dimension: Dimension

    init {
        val size = sqrt(entries.size.toDouble()).toInt()
        require(size * size == entries.size) { "Grid must be a square" }
        dimension = Dimension(size, size)
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
