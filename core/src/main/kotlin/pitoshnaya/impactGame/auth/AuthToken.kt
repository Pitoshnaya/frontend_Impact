package pitoshnaya.impactGame.auth

data class AuthToken(val value: String) {
    companion object {
        fun none() : AuthToken = AuthToken("")
    }

    fun isValid(): Boolean {
        // TODO нужна будет проверка на jwt в какой-то момент
        return value != ""
    }
}
