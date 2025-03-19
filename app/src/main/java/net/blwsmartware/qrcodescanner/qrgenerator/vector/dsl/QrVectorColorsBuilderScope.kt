package net.blwsmartware.qrcodescanner.qrgenerator.vector.dsl

import net.blwsmartware.qrcodescanner.qrgenerator.vector.style.IQrVectorColors
import net.blwsmartware.qrcodescanner.qrgenerator.vector.style.QrVectorColor


sealed interface QrVectorColorsBuilderScope : IQrVectorColors {
    override var ball: QrVectorColor
    override var dark: QrVectorColor
    override var frame: QrVectorColor
    override var light: QrVectorColor
}

