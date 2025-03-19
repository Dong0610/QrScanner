package net.blwsmartware.qrcodescanner.model

import net.blwsmartware.qrcodescanner.builder.reconvertUnicodeToEmoji
import java.io.Serializable


class EmailModel(var email: String = "", var subject: String = "", var body: String = "") :
    Serializable
fun EmailModel.toMailtoString(): String {
    return """
        MAILTO:$email
        SUBJECT:$subject
        BODY:$body
    """.trimIndent()
}
fun stringToMailModel(mailtoString: String): EmailModel {
    val lines = mailtoString.lines()
    val emailModel = EmailModel()

    lines.forEach { line ->
        when {
            line.startsWith("MAILTO:") -> emailModel.email = line.removePrefix("MAILTO:").trim().reconvertUnicodeToEmoji()
            line.startsWith("SUBJECT:") -> emailModel.subject = line.removePrefix("SUBJECT:").trim().reconvertUnicodeToEmoji()
            line.startsWith("BODY:") -> emailModel.body = line.removePrefix("BODY:").trim().reconvertUnicodeToEmoji()
        }
    }
    return emailModel
}

