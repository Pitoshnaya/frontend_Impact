package pitoshnaya.impact_game.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.viewport.ScreenViewport
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import pitoshnaya.impact_game.asset.Theme
import pitoshnaya.impact_game.auth.User
import pitoshnaya.impact_game.auth.Manager as AuthManager

class MainMenu: KtxScreen {
    private val wrapper = Stage(ScreenViewport())
    private val theme = Theme.default()

    override fun show() {
        if (AuthManager.getCurrentUser() != null) {
            // TODO transition to game scene
            return
        }

        createLoginForm()
    }

    override fun render(delta: Float) {
        wrapper.act(delta)
        wrapper.draw()
    }

    override fun resize(width: Int, height: Int) {
        wrapper.viewport.update(width, height, true)
    }

    override fun dispose() {
        wrapper.dispose()
    }

    private fun createLoginForm() {

        val nicknameInput = TextField("", theme)
        val passwordInput = TextField("", theme).apply {
            isPasswordMode = true
            setPasswordCharacter('*')
        }
        val loginButton = createLoginButton(nicknameInput, passwordInput)

        val table = Table()
        table.setFillParent(true)
        table.add(Label("Nickname:", theme))
        table.row()
        table.add(nicknameInput).width(300f).pad(10f)
        table.row()
        table.add(Label("Password:", theme))
        table.row()
        table.add(passwordInput).width(300f).pad(10f)
        table.row()
        table.add(loginButton).pad(20f)

        wrapper.addActor(table)
        Gdx.input.inputProcessor = wrapper
    }

    private fun createLoginButton(nicknameInput: TextField, passwordInput: TextField): TextButton{
        val loginButton = TextButton("Login", theme)
        loginButton.onClick {
            if (nicknameInput.text == "") {
                nicknameInput.style.fontColor = Color.RED
                false
            }

            if (passwordInput.text == "") {
                passwordInput.style.fontColor = Color.RED
                false
            }

            KtxAsync.launch {
                val user = User(nicknameInput.text)
                val result = user.login(passwordInput.text)
                if (result.isSuccess) {
                    AuthManager.setCurrentUser(user)
                    // TODO transition to CanvasMenu. Below is just a temp blocker to avoid several tokens creation
                    passwordInput.isDisabled = true
                    nicknameInput.isDisabled = true
                    loginButton.isDisabled = true
                } else {
                    nicknameInput.style.fontColor = Color.RED
                    passwordInput.style.fontColor = Color.RED
                }
            }
        }

        return loginButton
    }
}