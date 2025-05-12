package pitoshnaya.impactGame.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.Window

object Theme {
    private var cachedSkin: Skin? = null

    fun default(): Skin {
        cachedSkin?.let { return it }

        val skin = Skin()

        // Generate font
        val generator = FreeTypeFontGenerator(Gdx.files.internal("Osaka Regular-Mono.otf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            size = 24
            borderColor = Color.YELLOW
            borderWidth = 1f
        }
        val defaultFont = generator.generateFont(parameter)
        generator.dispose()

        // Create basic styles
        val defaultWindowStyle = Window.WindowStyle(defaultFont, Color.GREEN, null)
        val defaultLabelStyle = Label.LabelStyle(defaultFont, Color.YELLOW)

        val defaultButtonStyle = TextButton.TextButtonStyle().apply {
            font = defaultFont
            fontColor = Color.WHITE
            // Optional: create a blank texture for button background
            up = makeColoredDrawable(Color.DARK_GRAY)
            down = makeColoredDrawable(Color.GRAY)
        }

        val defaultTextFieldStyle = TextField.TextFieldStyle().apply {
            font = defaultFont
            fontColor = Color.WHITE
            background = makeColoredDrawable(Color.DARK_GRAY)
            cursor = makeColoredDrawable(Color.YELLOW)
        }

        // Add everything
        skin.add("default-font", defaultFont, BitmapFont::class.java)
        skin.add("default", defaultWindowStyle)
        skin.add("default", defaultLabelStyle)
        skin.add("default", defaultButtonStyle)
        skin.add("default", defaultTextFieldStyle)

        cachedSkin = skin
        return skin
    }

    private fun makeColoredDrawable(color: Color): Drawable {
        val pixmap = com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        return TextureRegionDrawable(texture)
    }
}
