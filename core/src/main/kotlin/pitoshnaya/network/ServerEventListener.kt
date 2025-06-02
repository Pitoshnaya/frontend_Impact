package pitoshnaya.network

interface ServerEventListener {
    fun handle(event: ServerEvent)
}
