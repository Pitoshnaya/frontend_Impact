package pitoshnaya.impactGame.network

import java.net.URI
import java.net.URL

data class Address(
    val host: String,
    val port: Int
) {
    val schema: String = "http"

    fun resolveURL(path: String): URL = URI(schema, null, host, port, path, null, null).toURL()

    override fun toString(): String = "$schema://$host:$port"
}
