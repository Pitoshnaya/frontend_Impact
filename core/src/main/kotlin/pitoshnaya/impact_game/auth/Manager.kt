package pitoshnaya.impact_game.auth

object Manager {
    private var currentUser: User? = null

    fun setCurrentUser(user: User?) {
        currentUser = user
    }

    fun getCurrentUser(): User? {
        return currentUser
    }
}