package pitoshnaya.impactGame.scene.gridGame.logic

import com.badlogic.gdx.graphics.Color
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import pitoshnaya.network.ClientEvent
import pitoshnaya.network.http.LongPollingClient
import pitoshnaya.network.ServerEvent
import pitoshnaya.network.ServerEventListener

private object ServerEvents {
    const val GRID_LOADED = "init"
    const val GRID_UPDATED = "gridUpdated"
    const val DISCONNECTED = "disconnected"
    const val PIXEL_DRAW = "pixelDraw"
}

val json = Gson()

class PixelDraw(x: Int, y: Int, color: Color) :
    ClientEvent(ServerEvents.PIXEL_DRAW, json.toJson(Pixel(x, y, color)))

internal class GridLoaded(data: String): ServerEvent(ServerEvents.GRID_LOADED, data) {
    val grid: Grid = Grid(json.fromJson(data, Array<Pixel>::class.java).asList())
}

internal class GridUpdated(data: String): ServerEvent(ServerEvents.GRID_LOADED, data) {
    val grid: Grid = Grid(json.fromJson(data, Array<Pixel>::class.java).asList())
}

internal class PixelColorChanged(data: String): ServerEvent(ServerEvents.PIXEL_DRAW, data) {
    val pixel: Pixel = json.fromJson(data, Pixel::class.java)
}

internal class Disconnected(data: String): ServerEvent(ServerEvents.DISCONNECTED, data)

class GridGameServer(private val networkClient: LongPollingClient) :
    ServerEventListener {
    private var isRunning = false

    private var refresher: Job? = null

    private var listener: ((event: ServerEvent) -> Unit)? = null

    init {
        networkClient.mapEvents(
            mapOf(
                ServerEvents.GRID_LOADED to "GET /grid/canvas",
                ServerEvents.PIXEL_DRAW to "PUT /grid/canvas",
                ServerEvents.GRID_UPDATED to "GET /grid/canvas",
                ServerEvents.DISCONNECTED to "disconnected"
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
        refresher?.cancel()
        networkClient.removeServerEventListener(this)
        networkClient.disconnect()
    }

    fun send(event: ClientEvent) {
        require(listener != null) { "Отправлять события на сервер без обработчика не имеет смысла" }

        KtxAsync.launch {
            networkClient.send(event)
        }
    }

    override fun handle(event: ServerEvent) {
        if (!isRunning || listener == null) {
            return
        }

        val serverEvent = when(event.name) {
            ServerEvents.PIXEL_DRAW -> PixelColorChanged(event.payload)

            ServerEvents.GRID_UPDATED -> GridUpdated(event.payload)

            ServerEvents.GRID_LOADED -> GridLoaded(event.payload)

            ServerEvents.DISCONNECTED -> Disconnected(event.payload)

            else -> throw IllegalArgumentException("Unsupported event received: ${event.name}")
        }

        listener?.invoke(serverEvent)
    }

    fun onEvent(run: (event: ServerEvent) -> Unit) {
        listener = run
    }

    /**
     * Эмулирует поведение так, будто сервер присылает события, поскольку вместо вебсокетов мы общаемся по http.
     */
    private fun simulateSync() {
        refresher = KtxAsync.launch {
            // Оба события в этом коде являются вымышленными. Они отправляются не с клиента, а с сервера.
            // Поэтому не нужно оборачивать их в отдельные структуры
            networkClient.send(ClientEvent(ServerEvents.GRID_LOADED))

            while (true) {
                delay(2000)
                if (isRunning) {
                    networkClient.send(ClientEvent(ServerEvents.GRID_UPDATED))
                }
            }
        }
    }
}
