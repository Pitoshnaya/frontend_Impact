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
    val content: Object? = null
) {
    companion object {
        suspend fun GET(url: URL, headers: Map<String, String> = emptyMap()) = JsonRequest("GET", url, headers).send()
        suspend fun POST(url: URL, content: Object? = null, headers: Map<String, String> = emptyMap()) =
            JsonRequest("POST", url, headers, content).send()
    }

    private suspend fun send(): HttpRequestResult {
        val isValidRequest = !(content !== null && method == "GET")
        check(isValidRequest)

        return httpRequest(
            url = url.toString(),
            method = method,
            headers = mapOf("Content-Type" to "application/json") + headers,
            content = if (content == null) null else serializer.toJson(content)
        )
    }
}
