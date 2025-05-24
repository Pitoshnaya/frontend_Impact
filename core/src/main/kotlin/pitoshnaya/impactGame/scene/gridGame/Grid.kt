package pitoshnaya.impactGame.scene.gridGame

import kotlin.math.sqrt

internal class Grid: Iterable<Pixel> {
    val dimension: Dimension

    val cells: Map<String, Pixel>

    constructor(entries: List<Pixel>) {
        val size = sqrt(entries.size.toDouble()).toInt()
        require(size * size == entries.size) { "Grid must be a square" }
        dimension = Dimension(size, size)

        cells = entries.associateBy { it.id }
    }

    override fun iterator(): Iterator<Pixel> {
        return object : Iterator<Pixel> {
            private var pointer = 0
            private val entries = cells.values.toList()

            override fun next(): Pixel {
                if (!hasNext()) {
                    throw NoSuchElementException("No more pixels exist")
                }

                return entries[pointer++]
            }

            override fun hasNext(): Boolean {
                return pointer < entries.size
            }
        }
    }

    fun draw(pixel: Pixel) {
        cells[pixel.id]!!.color = pixel.color
    }

    fun diff(from: Grid): List<Pixel> {
        return from.filter { cells[it.id]!!.color != it.color }
    }
}
