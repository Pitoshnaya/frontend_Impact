package pitoshnaya.impactGame.scene.gridGame

import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import pitoshnaya.impactGame.network.ClientEvent
import pitoshnaya.impactGame.network.LongPollingGameServer
import pitoshnaya.impactGame.network.ServerEvent
import pitoshnaya.impactGame.network.ServerEventListener

internal object GridServerEvent {
    const val INIT = "init"
    const val UPDATED = "gridUpdated"
    const val DISCONNECTED = "disconnected"
    const val PIXEL_DRAW = "pixelDraw"
}

internal object GridEvents {
    private val json = Gson()

    fun drawPixel(pixel: Pixel) = ClientEvent(GridServerEvent.PIXEL_DRAW, json.toJson(pixel))
}

internal class GridGameServer(private val networkClient: LongPollingGameServer = LongPollingGameServer) :
    ServerEventListener {
    private var isRunning = false

    private var refresher: Job? = null

    private var listener: ((event: ServerEvent) -> Unit)? = null

    private var isEmulationStarted = false

    init {
        networkClient.mapEvents(
            mapOf(
                GridServerEvent.INIT to "GET /grid/canvas",
                GridServerEvent.PIXEL_DRAW to "PUT /grid/canvas",
                GridServerEvent.UPDATED to "GET /grid/canvas",
                GridServerEvent.DISCONNECTED to "disconnected"
            )
        )
    }

    fun start() {
        if (isRunning) {
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

    fun send(event: ClientEvent) {
        KtxAsync.launch {
            networkClient.send(event)
        }
    }

    override fun handle(event: ServerEvent) {
        if (!isRunning) {
            return
        }

        // Часть эмуляции. Имитирует событие "холст обновился" при получении события "холст инициализирован".
        if (isEmulationStarted && event.name == GridServerEvent.INIT) {
            listener?.invoke(ServerEvent(GridServerEvent.UPDATED, event.payload))
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
            networkClient.send(ClientEvent(GridServerEvent.INIT))

            while (true) {
                delay(3000)
                if (isRunning) {
                    isEmulationStarted = true
                    networkClient.send(ClientEvent(GridServerEvent.INIT))
                }
            }
        }
    }
}
