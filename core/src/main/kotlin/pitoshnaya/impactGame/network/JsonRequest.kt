package pitoshnaya.impactGame.network

import com.google.gson.Gson
import ktx.async.HttpRequestResult
import ktx.async.httpRequest
import java.net.URL

private val serializer: Gson = Gson()

data class JsonRequest(
    val method: String,
    val url: URL,
    val headers: Map<String, String> = emptyMap(),
    val content: Any? = null
) {
    companion object {
        suspend fun get(url: URL, headers: Map<String, String> = emptyMap()) = JsonRequest("GET", url, headers).send()
        suspend fun post(url: URL, content: Any? = null, headers: Map<String, String> = emptyMap()) =
            JsonRequest("POST", url, headers, content).send()
        suspend fun put(url: URL, content: Any? = null, headers: Map<String, String> = emptyMap()) =
            JsonRequest("PUT", url, headers, content).send()
    }

    private suspend fun send(): HttpRequestResult {
        val isValidRequest = !(content !== null && method == "GET")
        check(isValidRequest)

        return httpRequest(
            url = url.toString(),
            method = method,
            headers = mapOf("Content-Type" to "application/json") + headers,
            content = getPayload()
        )
    }

    private fun getPayload(): String? {
        return when(content) {
            null -> null
            is String -> content
            else -> serializer.toJson(content)
        }
    }
}
