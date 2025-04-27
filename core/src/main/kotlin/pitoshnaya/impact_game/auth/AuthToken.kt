package pitoshnaya.impact_game.auth

data class AuthToken(val token: String) {
    companion object {
        fun none() : AuthToken = AuthToken("")
    }

    fun isValid(): Boolean {
        return token != ""
    }
}