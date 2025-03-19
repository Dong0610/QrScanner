package net.blwsmartware.qrcodescanner.qrgenerator.vector.dsl

import android.graphics.drawable.Drawable
import net.blwsmartware.qrcodescanner.qrgenerator.style.BitmapScale
import net.blwsmartware.qrcodescanner.qrgenerator.vector.style.IQrVectorBackground
import net.blwsmartware.qrcodescanner.qrgenerator.vector.style.QrVectorColor

sealed interface QrVectorBackgroundBuilderScope : IQrVectorBackground {

    override var drawable: Drawable?
    override var scale: BitmapScale
    override var color: QrVectorColor
}