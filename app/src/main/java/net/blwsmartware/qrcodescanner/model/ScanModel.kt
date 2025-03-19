package net.blwsmartware.qrcodescanner.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize



@Parcelize
data class ScanModel(
    var code: String = "",
    var bitmap: Bitmap? = null,
    var path:String="",
    var tYpe: ScanType = ScanType.QRCODE
) : Parcelable