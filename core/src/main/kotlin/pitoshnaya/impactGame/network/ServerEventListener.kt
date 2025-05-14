package pitoshnaya.impactGame.network

interface ServerEventListener {
    fun handle(event: ServerEvent)
}
