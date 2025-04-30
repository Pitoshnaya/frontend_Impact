package pitoshnaya.impact_game.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
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
        val statusBox = Label("", theme)
        statusBox.setAlignment(Align.center)
        val nicknameInput = TextField("", theme)
        val passwordInput = TextField("", theme).apply {
            isPasswordMode = true
            setPasswordCharacter('*')
        }
        val loginButton = createLoginButton(nicknameInput, passwordInput, statusBox)

        val table = Table()
        table.setFillParent(true)
        table.add(Label("Nickname", theme))
        table.row()
        table.add(nicknameInput).width(300f).pad(10f)
        table.row()
        table.add(Label("Password", theme))
        table.row()
        table.add(passwordInput).width(300f).pad(10f)
        table.row()
        table.add(statusBox).width(300f).pad(10f)
        table.row()
        table.add(loginButton).pad(20f)

        wrapper.addActor(table)
    }

    private fun createLoginButton(nicknameInput: TextField, passwordInput: TextField, statusBox: Label): TextButton {
        val loginButton = TextButton("Login", theme)
        loginButton.onClick {
            if (nicknameInput.text == "") {
                statusBox.setText("Login must not be empty")

                return@onClick
            }

            if (passwordInput.text == "") {
                statusBox.setText("Password must not be empty")

                return@onClick
            }

            KtxAsync.launch {
                val result = GameServer.login(nicknameInput.text, passwordInput.text)

                result
                    .onSuccess {
                        AuthManager.setCurrentUser(it)
                        SceneController.set<PlaceCanvas>()
                    }.onFailure {
                        statusBox.setText(it.message)
                    }
            }
        }

        return loginButton
    }
}