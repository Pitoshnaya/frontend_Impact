package pitoshnaya.impactGame.auth

data class User(private val name: String, private var token: AuthToken = AuthToken.none()) {
    init {
        require(name != "")
    }

    fun isAuthenticated(): Boolean {
        return token.isValid()
    }

    fun getToken(): AuthToken {
        return token
    }
}
