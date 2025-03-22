package net.blwsmartware.qrcodescanner.qrgenerator.vector.dsl

import android.graphics.drawable.Drawable
import net.blwsmartware.qrcodescanner.qrgenerator.vector.style.*
import net.blwsmartware.qrcodescanner.qrgenerator.style.BitmapScale

sealed interface QrVectorLogoBuilderScope : IQRVectorLogo {

    override var drawable: Drawable?
    override var size : Float
    override var padding : QrVectorLogoPadding
    override var shape: QrVectorLogoShape
    override var scale: BitmapScale
    override var backgroundColor : QrVectorColor
}

