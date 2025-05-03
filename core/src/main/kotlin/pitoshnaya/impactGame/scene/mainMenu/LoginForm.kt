package pitoshnaya.impactGame.scene.mainMenu

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.async.KtxAsync
import pitoshnaya.impactGame.auth.Manager
import pitoshnaya.impactGame.network.GameServer
import pitoshnaya.impactGame.scene.GridGame
import pitoshnaya.impactGame.scene.SceneController

internal class LoginForm(private val theme: Skin): Table() {
    init {
        val statusBox = Label("", theme)
        statusBox.setAlignment(Align.center)
        val nicknameInput = TextField("", theme)
        val passwordInput = TextField("", theme).apply {
            isPasswordMode = true
            setPasswordCharacter('*')
        }
        val loginButton = createLoginButton(nicknameInput, passwordInput, statusBox)

        setFillParent(true)
        add(statusBox).width(300f).pad(10f)
        row()
        add(Label("Nickname", theme))
        row()
        add(nicknameInput).width(300f).pad(10f)
        row()
        add(Label("Password", theme))
        row()
        add(passwordInput).width(300f).pad(10f)
        row()
        add(loginButton).pad(20f)
    }

    private fun createLoginButton(nicknameInput: TextField, passwordInput: TextField, statusBox: Label): TextButton {
        val loginButton = TextButton("Log in", theme)
        loginButton.onClick {
            if (nicknameInput.text == "") {
                statusBox.setText("Nickname must not be empty")

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
                        Manager.setCurrentUser(it)
                        SceneController.set<GridGame>()
                    }.onFailure {
                        statusBox.setText(it.message)
                    }
            }
        }

        return loginButton
    }
}
