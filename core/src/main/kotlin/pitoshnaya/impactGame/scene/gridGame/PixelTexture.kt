package pitoshnaya.impactGame.scene.gridGame

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture

internal object PixelTexture {
    private val cache = mutableMapOf<String, Texture>()

    fun forColor(color: Color): Texture {
        val hexColor = color.toString()
        if (!cache.containsKey(hexColor)) {
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
                setColor(color)
                fill()
            }
            val texture = Texture(pixmap)
            pixmap.dispose()

            cache.put(hexColor, texture)
        }

        return cache[hexColor]!!
    }
}
