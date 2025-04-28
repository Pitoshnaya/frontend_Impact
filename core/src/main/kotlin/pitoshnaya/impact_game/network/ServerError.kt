package pitoshnaya.impact_game.network

class ServerError(message: String): RuntimeException(message) {
    companion object {
        fun connectionRefused(): ServerError {
            return ServerError("Couldn't connect to server")
        }

        fun authenticationRequired(): ServerError {
            return ServerError("Server requires authentication")
        }
    }
}