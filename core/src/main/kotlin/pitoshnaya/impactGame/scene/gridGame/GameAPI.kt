package pitoshnaya.impactGame.scene.gridGame

import com.google.gson.Gson
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import pitoshnaya.impactGame.network.GameServer

internal class GameAPI {
    private val json = Gson()
    private val fetchCanvas = "/grid/canvas"

    fun getGrid(success: (Grid) -> Unit) {
        KtxAsync.launch {
            GameServer.consume(fetchCanvas)
                .onSuccess {
                    val grid = Grid(json.fromJson(it, Array<Pixel>::class.java).asList())
                    success(grid)
                }.onFailure {
                    // TODO по сути рантайм. Надо будет как-то обработать/записать в логи, если игра не прервалась
                    throw it
                }
        }
    }
}
