package pitoshnaya.impactGame.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align

class ColorPicker(onPick: (pickedColor: Color) -> Unit, theme: Skin) : Table() {
    init {
        align(Align.bottomLeft)
        val colorPreview = Image(createColoredDrawable(Color.WHITE))
        colorPreview.color = Color.BLACK

        val redSlider = Slider(0f, 1f, 0.01f, false, theme)
        redSlider.color = Color.RED
        val greenSlider = Slider(0f, 1f, 0.01f, false, theme)
        greenSlider.color = Color.GREEN
        val blueSlider = Slider(0f, 1f, 0.01f, false, theme)
        blueSlider.color = Color.BLUE

        val handler = object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                updateColor()
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                updateColor()
            }

            private fun updateColor() {
                val color = Color(redSlider.value, greenSlider.value, blueSlider.value, 1f)
                colorPreview.color = color
                onPick(color)
            }
        }

        listOf(redSlider, greenSlider, blueSlider).forEach { slider -> slider.addListener(handler) }

        add(Label("R", theme)).colspan(1)
        add(redSlider).width(200f).colspan(4).align(Align.left)
        row()
        add(Label("G", theme)).colspan(1)
        add(greenSlider).width(200f).colspan(4).align(Align.left)
        row()
        add(Label("B", theme)).colspan(1)
        add(blueSlider).width(200f).colspan(4).align(Align.left)
        row()
        add(colorPreview).colspan(5).height(20f).fill()
    }
}
