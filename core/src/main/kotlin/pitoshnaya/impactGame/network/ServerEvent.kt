package pitoshnaya.impactGame.network

import java.time.LocalDateTime

data class ServerEvent(val name: String, val payload: String = "", val sentAt: LocalDateTime = LocalDateTime.now())
