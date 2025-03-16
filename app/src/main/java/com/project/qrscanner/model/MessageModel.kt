package com.project.qrscanner.model

import com.project.qrscanner.builder.reconvertUnicodeToEmoji
import java.io.Serializable


class MessageModel(var phone: String = "", var message: String = "") :
    Serializable

fun MessageModel.toSmsString(): String {
    return "SMS: $phone: $message"
}

fun parseSmsString(smsString: String): MessageModel {
    val messageModel = MessageModel()
    if (smsString.startsWith("SMS:")) {
        val content = smsString.removePrefix("SMS:").trim()
        val parts = content.split(":", limit = 2)
        if (parts.size == 2) {
            messageModel.phone = parts[0].trim().reconvertUnicodeToEmoji()
            messageModel.message = parts[1].trim().reconvertUnicodeToEmoji()
        }
    }
    return messageModel
}