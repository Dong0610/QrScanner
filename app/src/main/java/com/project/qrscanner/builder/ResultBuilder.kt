package com.project.qrscanner.builder

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.dong.baselib.widget.fromColor
import com.project.qrscanner.R
import com.project.qrscanner.databinding.ItemReslutViewBinding
import com.project.qrscanner.model.ContactModel
import com.project.qrscanner.model.EmailModel
import com.project.qrscanner.model.EventModel
import com.project.qrscanner.model.LocationModel
import com.project.qrscanner.model.MessageModel
import com.project.qrscanner.model.QrType
import com.project.qrscanner.model.WifiModel
import java.net.URI
import java.net.URL

fun detectQrType(content: String): QrType {
    if (content.isBlank()) return QrType.TEXT

    return when {
        // URL detection with comprehensive pattern
        content.matches(Regex(
            "^(https?://|www\\.)" +  // Protocol or www
                    "[a-zA-Z0-9-]+" +       // Domain name
                    "(?:\\.[a-zA-Z0-9-]+)*" + // Additional domain levels
                    "(?::\\d{1,5})?" +      // Port number
                    "(?:/[\\w\\-./]*)*" +   // Path
                    "(?:\\?[\\w=&\\-%.]*?)?" + // Query parameters
                    "(?:#[\\w]*)?$",        // Fragment
            RegexOption.IGNORE_CASE
        )) ||
                content.startsWith("http://") ||
                content.startsWith("https://") ||
                content.startsWith("www.") ||
                try {
                    val url = URL(content)
                    val uri = URI(url.protocol, url.userInfo, url.host, url.port, url.path, url.query, url.ref)
                    uri.scheme != null && uri.host != null &&
                            (uri.scheme.equals("http", true) || uri.scheme.equals("https", true))
                } catch (e: Exception) {
                    false
                } -> QrType.URL

        // Email
        content.startsWith("mailto:") ||
                content.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) -> QrType.EMAIL

        // Phone
        content.startsWith("tel:") ||
                content.matches(Regex("^\\+?[0-9()-]{10,}$")) -> QrType.PHONE

        content.startsWith("sms:") ||
                content.startsWith("smsto:") -> QrType.SMS

        content.startsWith("WIFI:") -> QrType.WIFI

        content.startsWith("geo:") ||
                content.matches(Regex("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$")) -> QrType.LOCATION

        content.startsWith("BEGIN:VCARD") ||
                content.startsWith("MECARD:") -> QrType.CONTACT

        // Event
        content.startsWith("BEGIN:VEVENT") -> QrType.EVENT

        // Barcode
        content.matches(Regex("^[0-9]{8,14}$")) -> QrType.BARCODE

        else -> QrType.TEXT
    }
}

fun AppCompatButton.setState(state:Boolean){
    if(state){
        setBackgroundResource(R.drawable.bg_button_active)
        setTextColor(fromColor("ffffff"))
    }else{
        setBackgroundResource(R.drawable.bg_button_disabled)
        setTextColor(fromColor("#B6B6B4"))
    }
}


fun AppCompatActivity.resultText(lineLayout: LinearLayout, text: String) {
    lineLayout.removeAllViews()
    val itemBinding = ItemReslutViewBinding.inflate(layoutInflater, lineLayout, false)
    with(itemBinding) {
        resultTitle.text = getString(R.string.text)
        resultContent.text = text
    }
    lineLayout.addView(itemBinding.root)
}

fun AppCompatActivity.resultBarcode(lineLayout: LinearLayout, text: String) {
    lineLayout.removeAllViews()
    val itemBinding = ItemReslutViewBinding.inflate(layoutInflater, lineLayout, false)
    with(itemBinding) {
        resultTitle.text = getString(R.string.value)
        resultContent.text = text
    }
    lineLayout.addView(itemBinding.root)
}




fun AppCompatActivity.resultUrl(lineLayout: LinearLayout, text: String) {
    lineLayout.removeAllViews()
    val itemBinding = ItemReslutViewBinding.inflate(layoutInflater, lineLayout, false)
    with(itemBinding) {
        resultTitle.text = getString(R.string.url)
        resultContent.text = text
    }
    lineLayout.addView(itemBinding.root)
}

fun AppCompatActivity.resultContact(linearLayout: LinearLayout, contact: ContactModel) {
    linearLayout.removeAllViews()

    val contactItems = listOfNotNull(
        if (contact.name.isNotEmpty()) R.string.name to contact.name else null,
        if (contact.company.isNotEmpty()) R.string.company to contact.company else null,
        if (contact.phone.isNotEmpty()) R.string.phone to contact.phone else null,
        if (contact.email.isNotEmpty()) R.string.email to contact.email else null,
        if (contact.address.isNotEmpty()) R.string.address to contact.address else null,
        if (contact.note.isNotEmpty()) R.string.note to contact.note else null
    )
    contactItems.forEachIndexed { index, (titleResId, content) ->
        val itemBinding = ItemReslutViewBinding.inflate(layoutInflater, linearLayout, false)
        with(itemBinding) {
            resultTitle.text = getString(titleResId)
            resultContent.text = content
        }
        linearLayout.addView(itemBinding.root)
    }
}

fun AppCompatActivity.resultEmail(linearLayout: LinearLayout, email: EmailModel) {
    linearLayout.removeAllViews()

    val emailItems = listOfNotNull(
        if (email.email.isNotEmpty()) R.string.email to email.email else null,
        if (email.subject.isNotEmpty()) R.string.subject to email.subject else null,
        if (email.body.isNotEmpty()) R.string.body to email.body else null
    )
    emailItems.forEachIndexed { index, (titleResId, content) ->
        val itemBinding = ItemReslutViewBinding.inflate(layoutInflater, linearLayout, false)
        with(itemBinding) {
            resultTitle.text = getString(titleResId)
            resultContent.text = content
        }
        linearLayout.addView(itemBinding.root)
    }
}

fun AppCompatActivity.resultEvent(linearLayout: LinearLayout, event: EventModel) {
    linearLayout.removeAllViews()

    val eventItems = listOfNotNull(
        if (event.eventName.isNotEmpty()) R.string.event_name to event.eventName else null,
        if (event.startTime.isNotEmpty()) R.string.event_start_time to event.startTime else null,
        if (event.endTime.isNotEmpty()) R.string.event_end_time to event.endTime else null,
        if (event.location.isNotEmpty()) R.string.event_location to event.location else null,
        if (event.description.isNotEmpty()) R.string.event_description to event.description else null
    )
    eventItems.forEachIndexed { index, (titleResId, content) ->
        val itemBinding = ItemReslutViewBinding.inflate(layoutInflater, linearLayout, false)
        with(itemBinding) {
            resultTitle.text = getString(titleResId)
            resultContent.text = content
        }
        linearLayout.addView(itemBinding.root)
    }
}

fun AppCompatActivity.resultMessage(linearLayout: LinearLayout, message: MessageModel) {
    linearLayout.removeAllViews()
    val messageItems = listOfNotNull(
        if (message.phone.isNotEmpty()) R.string.phone to message.phone else null,
        if (message.message.isNotEmpty()) R.string.message to message.message else null

    )
    messageItems.forEachIndexed { index, (titleResId, content) ->
        val itemBinding = ItemReslutViewBinding.inflate(layoutInflater, linearLayout, false)
        with(itemBinding) {
            resultTitle.text = getString(titleResId)
            resultContent.text = content
        }
        linearLayout.addView(itemBinding.root)
    }
}

fun AppCompatActivity.resultLocation(linearLayout: LinearLayout, message: LocationModel) {
    linearLayout.removeAllViews()
    val messageItems = listOfNotNull(
        if (message.longitude.isNotEmpty()) R.string.longtitude to message.longitude else null,
        if (message.latitude.isNotEmpty()) R.string.latitude to message.latitude else null,
        if(message.query.isNotEmpty()) R.string.query to message.query else null
    )
    messageItems.forEachIndexed { index, (titleResId, content) ->
        val itemBinding = ItemReslutViewBinding.inflate(layoutInflater, linearLayout, false)
        with(itemBinding) {
            resultTitle.text = "${getString(titleResId)}"
            resultContent.text = content
        }
        linearLayout.addView(itemBinding.root)
    }
}


fun AppCompatActivity.resultWifi(linearLayout: LinearLayout, wifi: WifiModel) {
    linearLayout.removeAllViews()

    val wifiItems = listOfNotNull(
        if (wifi.name.isNotEmpty()) R.string.name to wifi.name else null,
        if (wifi.pass.isNotEmpty()) R.string.password to wifi.pass else null,
        if (wifi.security.isNotEmpty()) R.string.security to wifi.security else null
    )

    // Add items to linearLayout with spacing
    wifiItems.forEachIndexed { index, (titleResId, content) ->
        val itemBinding = ItemReslutViewBinding.inflate(layoutInflater, linearLayout, false)
        with(itemBinding) {
            resultTitle.text = getString(titleResId)
            resultContent.text = content
        }
        linearLayout.addView(itemBinding.root)
    }

}

fun AppCompatActivity.connectToWifi(wifi: WifiModel) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // For Android 10 and above
        val suggestion = WifiNetworkSuggestion.Builder()
            .setSsid(wifi.name)
            .setWpa2Passphrase(wifi.pass)
            .build()

        val suggestions = listOf(suggestion)
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val status = wifiManager.addNetworkSuggestions(suggestions)
        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            Toast.makeText(this, R.string.wifi_connecting, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.wifi_error_connecting, Toast.LENGTH_SHORT).show()
        }
    } else {
        // For Android 9 and below
        @Suppress("DEPRECATION")
        val wifiConfig = WifiConfiguration().apply {
            SSID = "\"${wifi.name}\""
            preSharedKey = "\"${wifi.pass}\""
        }

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        @Suppress("DEPRECATION")
        val netId = wifiManager.addNetwork(wifiConfig)

        if (netId != -1) {
            @Suppress("DEPRECATION")
            wifiManager.disconnect()
            @Suppress("DEPRECATION")
            wifiManager.enableNetwork(netId, true)
            @Suppress("DEPRECATION")
            wifiManager.reconnect()
            Toast.makeText(this, R.string.wifi_connecting, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.wifi_error_connecting, Toast.LENGTH_SHORT).show()
        }
    }
}









































