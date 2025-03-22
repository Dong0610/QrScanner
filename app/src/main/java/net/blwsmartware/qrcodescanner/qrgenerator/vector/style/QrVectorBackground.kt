package net.blwsmartware.qrcodescanner.qrgenerator.vector.style

import android.graphics.drawable.Drawable
import net.blwsmartware.qrcodescanner.qrgenerator.style.BitmapScale

interface IQrVectorBackground  {

    /**
     * Background image of QR code. Applied aon top of [color]
     * */
    val drawable: Drawable?
    val scale: BitmapScale
    val color : QrVectorColor
}


data class QrVectorBackground(
    override val drawable: Drawable? = null,
    override val scale: BitmapScale = BitmapScale.FitXY,
    override val color : QrVectorColor = QrVectorColor.Transparent
) : IQrVectorBackground