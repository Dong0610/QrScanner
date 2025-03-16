package com.project.qrscanner.model

import android.graphics.Color
import com.dong.baselib.builder.fromColor
import com.project.qrscanner.R
import java.io.Serializable


enum class QrType {
    TEXT, SMS, PHONE, EMAIL, WIFI, LOCATION, CONTACT, URL, BARCODE, EVENT
}

enum class CreateType {
    CREATE, SCAN
}

enum class ScanType {
    QRCODE, BARCODE
}

data class QrStyle(
    var foregroundColor: String = "#000000",
    var backgroundColor: String = "#FFFFFF",
    var icon: String? = ""
) : Serializable

fun getQrType(data: String): QrType {
    return when (data) {
        "3" -> QrType.TEXT
        "6" -> QrType.SMS
        "8" -> QrType.PHONE
        "5" -> QrType.EMAIL
        "10" -> QrType.WIFI
        "7" -> QrType.LOCATION
        "4" -> QrType.CONTACT
        "2" -> QrType.URL
        "11" -> QrType.BARCODE
        "9" -> QrType.EVENT
        else -> QrType.TEXT
    }
}


fun getQrType(type: QrType): Int {
    return when (type) {
        QrType.TEXT -> R.string.text
        QrType.SMS -> R.string.sms
        QrType.PHONE -> R.string.phone
        QrType.EMAIL -> R.string.email
        QrType.WIFI -> R.string.wifi
        QrType.LOCATION -> R.string.location
        QrType.CONTACT -> R.string.contact
        QrType.URL -> R.string.url
        QrType.BARCODE -> R.string.barcode
        QrType.EVENT -> R.string.calendar
    }
}

data class RevertData(
    var icon: Int = -1,
    var color: Int = fromColor("000000"),
    var title: Int = -1
) : Serializable

fun getTypeRevert(type: QrType): RevertData {
    val r = RevertData()
    r.title = getQrType(type)
    r.icon = when (type) {
        QrType.TEXT -> R.drawable.icon_create_1
        QrType.SMS -> R.drawable.icon_create_4
        QrType.PHONE -> R.drawable.icon_create_6
        QrType.EMAIL -> R.drawable.icon_create_3
        QrType.WIFI -> R.drawable.icon_create_8
        QrType.LOCATION -> R.drawable.icon_create_5
        QrType.CONTACT -> R.drawable.icon_create_2
        QrType.URL -> R.drawable.icon_create_10
        QrType.BARCODE -> R.drawable.ic_view_barcode
        QrType.EVENT -> R.drawable.icon_create_7
    }

    r.color = when (type) {
        QrType.TEXT -> Color.parseColor("#FF6F31")
        QrType.SMS -> Color.parseColor("#FF0F82")
        QrType.PHONE -> Color.parseColor("#069B80")
        QrType.EMAIL -> Color.parseColor("#001AA4")
        QrType.WIFI -> Color.parseColor("#0A5F9C")
        QrType.LOCATION -> Color.parseColor("#F40000")
        QrType.CONTACT -> Color.parseColor("#177E34")
        QrType.URL -> Color.parseColor("#0580DA")
        QrType.BARCODE -> Color.parseColor("#FF9F0F")
        QrType.EVENT -> Color.parseColor("#630EB8")
    }

    return r
}

























