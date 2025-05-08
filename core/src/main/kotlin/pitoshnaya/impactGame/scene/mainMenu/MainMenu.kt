package pitoshnaya.impactGame.scene.mainMenu

import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onClick
import pitoshnaya.impactGame.auth.AuthServer
import pitoshnaya.impactGame.network.GameServer
import pitoshnaya.impactGame.scene.gridGame.GridGame
import pitoshnaya.impactGame.scene.Scene
import pitoshnaya.impactGame.scene.SceneController

class MainMenu : Scene() {
    private val loginForm = LoginForm(theme)
    private val registrationForm = RegistrationForm(theme)

    override fun load(): Boolean {
        if (AuthServer.tryToAutologin() && GameServer.connect()) {
            SceneController.set<GridGame>()

            return false
        }

        loginForm.isVisible = true
        registrationForm.isVisible = false

        val switchToRegistration = TextButton("Register first", theme)
        switchToRegistration.isVisible = true
        switchToRegistration.setPosition(25f, 25f)

        val switchToLogin = TextButton("Log in", theme)
        switchToLogin.setPosition(25f, 25f)
        switchToLogin.isVisible = false

        switchToRegistration.onClick {
            isVisible = false
            loginForm.isVisible = false
            registrationForm.isVisible = true
            switchToLogin.isVisible = true
        }
        switchToLogin.onClick {
            isVisible = false
            loginForm.isVisible = true
            registrationForm.isVisible = false
            switchToRegistration.isVisible = true
        }

        wrapper.addActor(loginForm)
        wrapper.addActor(registrationForm)
        wrapper.addActor(switchToLogin)
        wrapper.addActor(switchToRegistration)

        return true
    }
}
