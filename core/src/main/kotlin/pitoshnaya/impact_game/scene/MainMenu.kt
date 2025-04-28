package pitoshnaya.impact_game.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.async.KtxAsync
import pitoshnaya.impact_game.network.GameServer
import pitoshnaya.impact_game.auth.Manager as AuthManager

class MainMenu : Scene() {
    override fun load() {
        if (AuthManager.getCurrentUser() != null) {
            SceneController.set<PlaceCanvas>()
        } else {
            createLoginForm()
        }
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
    }

    private fun createLoginButton(nicknameInput: TextField, passwordInput: TextField): TextButton{
        val loginButton = TextButton("Login", theme)
        loginButton.onClick {
            if (nicknameInput.text == "") {
                nicknameInput.style.fontColor = Color.RED
                return@onClick
            }

            if (passwordInput.text == "") {
                passwordInput.style.fontColor = Color.RED
                return@onClick
            }

            KtxAsync.launch {
                val result = GameServer.login(nicknameInput.text, passwordInput.text)
                if (result.isSuccess) {
                    val user = result.getOrThrow()
                    AuthManager.setCurrentUser(user)
                    SceneController.set<PlaceCanvas>()
                } else {
                    nicknameInput.style.fontColor = Color.RED
                    passwordInput.style.fontColor = Color.RED
                }
            }
        }

        return loginButton
    }
}