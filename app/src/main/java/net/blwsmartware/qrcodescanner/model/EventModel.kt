package net.blwsmartware.qrcodescanner.model

import net.blwsmartware.qrcodescanner.builder.convertEmojisToUnicode
import net.blwsmartware.qrcodescanner.builder.reconvertUnicodeToEmoji
import java.io.Serializable

data class EventModel(
    var eventName: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var location: String = "",
    var description: String = "",
) :Serializable {

    fun toQRCodeString(): String {
        return """
            BEGIN:VEVENT
            SUMMARY:${eventName.convertEmojisToUnicode()}
            DTSTART:$startTime
            DTEND:$endTime
            LOCATION:${location.convertEmojisToUnicode()}
            DESCRIPTION:${description.convertEmojisToUnicode()}
            END:VEVENT
        """.trimIndent()
    }

    companion object {
        fun fromQRCodeString(qrCodeString: String): EventModel? {
            try {
                val eventName =
                    Regex("SUMMARY:(.*)").find(qrCodeString)?.groupValues?.get(1)?.trim() ?: ""
                val startTime =
                    Regex("DTSTART:(.*)").find(qrCodeString)?.groupValues?.get(1)?.trim() ?: ""
                val endTime =
                    Regex("DTEND:(.*)").find(qrCodeString)?.groupValues?.get(1)?.trim() ?: ""
                val location =
                    Regex("LOCATION:(.*)").find(qrCodeString)?.groupValues?.get(1)?.trim() ?: ""
                val description =
                    Regex("DESCRIPTION:(.*)").find(qrCodeString)?.groupValues?.get(1)?.trim() ?: ""
                return EventModel(
                    eventName.reconvertUnicodeToEmoji(),
                    startTime,
                    endTime,
                    location.reconvertUnicodeToEmoji(),
                    description.reconvertUnicodeToEmoji()
                )
            } catch (e: Exception) {

                return null
            }
        }
    }
}
