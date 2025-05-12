package pitoshnaya.impactGame.scene.gridGame

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
}
