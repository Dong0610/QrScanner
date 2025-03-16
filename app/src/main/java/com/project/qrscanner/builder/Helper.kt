package com.project.qrscanner.builder

import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


fun String?.reconvertUnicodeToEmoji(): String {
    val regex = Regex("""\\u([0-9A-Fa-f]{4})""")
    return if (this == null) ""
    else {
        regex.replace(this) { matchResult ->
            val codePoint =
                matchResult.groupValues[1].toInt(16)
            String(Character.toChars(codePoint))
        }
    }
}

fun String?.convertEmojisToUnicode(): String {
    if (this.isNullOrEmpty()) return ""

    val unicodeString = StringBuilder()
    val codePoints = this.codePoints().toArray()

    for (codePoint in codePoints) {
        unicodeString.append(String.format("\\u%04X", codePoint))
    }
    return unicodeString.toString()
}

fun timeNow(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        currentDateTime.format(formatter)
    } else {
        val date = Date()
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        formatter.format(date)
    }
}

fun TextView.listenError(error: MutableLiveData<String>, lifecycleOwner: LifecycleOwner) {
    error.observe(lifecycleOwner) { errorMessage ->
        text = errorMessage?.reconvertUnicodeToEmoji() ?: ""
        visibility = if (errorMessage.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
    }
}

fun MutableLiveData<String>.textViewError(textView: TextView, lifecycleOwner: LifecycleOwner) {
    observe(lifecycleOwner) { errorMessage ->
        textView.text = errorMessage?.reconvertUnicodeToEmoji() ?: ""
        textView.visibility = if (errorMessage.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
    }
}