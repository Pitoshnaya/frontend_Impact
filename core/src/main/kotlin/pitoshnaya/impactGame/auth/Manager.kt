package pitoshnaya.impactGame.auth

object Manager {
    private var currentUser: User? = null

    fun setCurrentUser(user: User?) {
        currentUser = user
    }

    fun getCurrentUser(): User? {
        return currentUser
    }
}
