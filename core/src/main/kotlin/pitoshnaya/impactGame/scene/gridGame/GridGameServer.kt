package pitoshnaya.impactGame.scene.gridGame

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import pitoshnaya.impactGame.network.ClientEvent
import pitoshnaya.impactGame.network.LongPollingGameServer
import pitoshnaya.impactGame.network.NetworkClient
import pitoshnaya.impactGame.network.ServerEvent
import pitoshnaya.impactGame.network.ServerEventListener

internal object GridEvent {
    const val INIT = "/grid/canvas"
    const val UPDATED = "TODO"
    const val DISCONNECTED = "disconnected"
}

internal class GridGameServer(private val networkClient: NetworkClient = LongPollingGameServer): ServerEventListener {
    private var isRunning = false

    private var refresher: Job? = null

    private var listener: ((event: ServerEvent) -> Unit)? = null

    private var isEmulationStarted = false

    fun start() {
        if(isRunning) {
            return
        }
        isRunning = true

        networkClient.addServerEventListener(this)
        networkClient.connect()

        simulateSync()
    }

    fun stop() {
        isRunning = false
        isEmulationStarted = false
        refresher?.cancel()
        networkClient.removeServerEventListener(this)
        networkClient.disconnect()
    }

    override fun handle(event: ServerEvent) {
        if (!isRunning) {
            return
        }

        // Часть эмуляции. Имитирует событие "холст обновился" при получении события "холст инициализирован".
        if (isEmulationStarted && event.name == GridEvent.INIT) {
            print("emulating")
            listener?.invoke(ServerEvent(GridEvent.UPDATED, event.payload))
        } else {
            listener?.invoke(event)
        }
    }

    fun onEvent(run: (event: ServerEvent) -> Unit) {
        listener = run
    }

    /**
     * Эмулирует поведение так, будто сервер присылает события, поскольку вместо вебсокетов мы общаемся по http.
     */
    private fun simulateSync() {
        refresher = KtxAsync.launch {
            networkClient.send(ClientEvent(GridEvent.INIT))

            while (true) {
                delay(3000)
                if (isRunning) {
                    isEmulationStarted = true
                    networkClient.send(ClientEvent(GridEvent.INIT))
                }
            }
        }
    }
}
