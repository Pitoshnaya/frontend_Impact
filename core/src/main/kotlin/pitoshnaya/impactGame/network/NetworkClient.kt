package pitoshnaya.impactGame.network

interface NetworkClient {
    fun connect()
    fun disconnect()
    suspend fun send(event: ClientEvent)
    fun addServerEventListener(listener: ServerEventListener)
    fun removeServerEventListener(listener: ServerEventListener)
}
