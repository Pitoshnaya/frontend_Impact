package pitoshnaya.network

import java.time.LocalDateTime

open class ServerEvent(val name: String, val payload: String = "", val sentAt: LocalDateTime = LocalDateTime.now())
