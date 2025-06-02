package pitoshnaya.impactGame.di

import pitoshnaya.impactGame.scene.gridGame.GridGame
import pitoshnaya.impactGame.scene.gridGame.logic.GridGameServer
import pitoshnaya.impactGame.scene.mainMenu.MainMenu
import pitoshnaya.network.http.LongPollingClient
import pitoshnaya.network.NetworkClient
import kotlin.reflect.KClass

object ServiceContainer {
    private val resolvers = mutableMapOf<KClass<*>, () -> Any>()
    private val instances = mutableMapOf<KClass<*>, Any>()

    init {
        // Декларируем все сервисы в реестре, чтобы резолвить их в дальнейшем и кэшировать по типу
        register(LongPollingClient::class) { LongPollingClient() }
        register(NetworkClient::class) { get<LongPollingClient>() }
        register(GridGameServer::class) { GridGameServer(get()) }

        register(MainMenu::class) { MainMenu() }
        register(GridGame::class) { GridGame(get()) }
    }

    fun <T : Any, S : T> register(service: KClass<T>, provider: () -> S) {
        resolvers[service] = provider
    }

    inline fun <reified T : Any> get(): T = get(T::class)

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(clazz: KClass<T>): T {
        return instances.getOrPut(clazz) { resolvers[clazz]?.invoke() as T } as T
    }
}
