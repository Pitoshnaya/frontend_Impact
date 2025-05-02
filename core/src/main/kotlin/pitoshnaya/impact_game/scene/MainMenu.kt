package pitoshnaya.impact_game.scene

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

// TODO rename package impact_game to impactGame
class MainMenu : Scene() {
    private val loginForm = Table()
    private val registrationForm = Table()

    override fun load() {
        if (AuthManager.getCurrentUser() != null) {
            SceneController.set<PlaceCanvas>()
        } else {
            addLoginForm()
            addRegistrationForm()
        }
    }

    private fun addLoginForm() {
        val statusBox = Label("", theme)
        statusBox.setAlignment(Align.center)
        val nicknameInput = TextField("", theme)
        val passwordInput = TextField("", theme).apply {
            isPasswordMode = true
            setPasswordCharacter('*')
        }
        val loginButton = createLoginButton(nicknameInput, passwordInput, statusBox)
        val switchToRegistration = TextButton("Register first", theme)
        switchToRegistration.onClick {
            loginForm.isVisible = false
            registrationForm.isVisible = true
        }

        loginForm.setFillParent(true)
        loginForm.add(statusBox).width(300f).pad(10f)
        loginForm.row()
        loginForm.add(Label("Nickname", theme))
        loginForm.row()
        loginForm.add(nicknameInput).width(300f).pad(10f)
        loginForm.row()
        loginForm.add(Label("Password", theme))
        loginForm.row()
        loginForm.add(passwordInput).width(300f).pad(10f)
        loginForm.row()
        loginForm.add(loginButton).space(20f)
        loginForm.row()
        loginForm.add(switchToRegistration).pad(20f)

        wrapper.addActor(loginForm)
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
                        AuthManager.setCurrentUser(it)
                        SceneController.set<PlaceCanvas>()
                    }.onFailure {
                        statusBox.setText(it.message)
                    }
            }
        }

        return loginButton
    }

    private fun addRegistrationForm() {
        val statusBox = Label("", theme)
        statusBox.setAlignment(Align.center)
        val nicknameInput = TextField("", theme)
        val passwordInput = TextField("", theme).apply {
            isPasswordMode = true
            setPasswordCharacter('*')
        }
        val switchToLogin = TextButton("Already registered?", theme)
        switchToLogin.onClick {
            loginForm.isVisible = true
            registrationForm.isVisible = false
        }
        val registrationButton = createRegistrationButton(nicknameInput, passwordInput, statusBox)

        registrationForm.setFillParent(true)
        registrationForm.add(statusBox).width(300f).pad(10f)
        registrationForm.row()
        registrationForm.add(Label("Nickname", theme))
        registrationForm.row()
        registrationForm.add(nicknameInput).width(300f).pad(10f)
        registrationForm.row()
        registrationForm.add(Label("Password", theme))
        registrationForm.row()
        registrationForm.add(passwordInput).width(300f).pad(10f)
        registrationForm.row()
        registrationForm.add(registrationButton).pad(20f)
        registrationForm.row()
        registrationForm.add(switchToLogin).pad(20f)

        registrationForm.isVisible = false

        wrapper.addActor(registrationForm)
    }

    private fun createRegistrationButton(
        nicknameInput: TextField,
        passwordInput: TextField,
        statusBox: Label
    ): TextButton {
        val registrationButton = TextButton("Register", theme)

        registrationButton.onClick {
            if (nicknameInput.text == "") {
                statusBox.setText("Nickname must not be empty")

                return@onClick
            }

            if (passwordInput.text == "") {
                statusBox.setText("Password must not be empty")

                return@onClick
            }

            KtxAsync.launch {
                val result = GameServer.createAccount(nicknameInput.text, passwordInput.text)

                result
                    .onSuccess {
                        AuthManager.setCurrentUser(it)
                        SceneController.set<PlaceCanvas>()
                    }.onFailure {
                        statusBox.setText(it.message)
                    }
            }
        }

        return registrationButton
    }
}
