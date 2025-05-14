package pitoshnaya.impactGame.network

data class ServerError(override val message: String): RuntimeException(message) {
    companion object {
        fun connectionRefused(): ServerError {
            return ServerError("Couldn't connect to server")
        }

        fun authenticationRequired(): ServerError {
            return ServerError("Server requires authentication")
        }

        fun wrongCredentials(): ServerError {
            return ServerError("Wrong credentials")
        }
    }
}
