package net.blwsmartware.qrcodescanner.qrgenerator.vector.dsl

import net.blwsmartware.qrcodescanner.qrgenerator.QrErrorCorrectionLevel
import net.blwsmartware.qrcodescanner.qrgenerator.QrErrorCorrectionLevel.Auto
import net.blwsmartware.qrcodescanner.qrgenerator.style.QrShape
import net.blwsmartware.qrcodescanner.qrgenerator.vector.style.QrVectorColor

sealed interface QrVectorOptionsBuilderScope  {

    /**
     * Padding of the QR code pattern from drawable border.
     * Should be from 0 to 0.5. Default value is 0
     * */
    var padding: Float

    /**
     * Level of error correction.
     * Determines part of qr code that can be corrupted or used for a logo.
     * [Auto] by default
     * */
    var errorCorrectionLevel: QrErrorCorrectionLevel

    /**
     * Shape of the QR code pattert
     * */
    var codeShape : QrShape

    /**
     * Enable 4th qr code eye. False by default.
     * This eye can overwrite an alignment eye and make QR code harder to scan.
     * */
    var fourthEyeEnabled : Boolean

    /**
     * Offset of the QR code pattern relative to padding size.
     * X and Y should be from -1 to 1. Both are 0 by default
     * */
    fun offset(x : Float, y : Float)

    /**
     * Shapes of QR code elements.
     * */
    fun shapes(centralSymmetry : Boolean = true, block: QrVectorShapesBuilderScope.() -> Unit)

    /**
     * Colors of QR code elements.
     * Background color is available in [background] builder.
     * Logo background color is available in [logo] builder
     * */
    fun colors(block: QrVectorColorsBuilderScope.() -> Unit)

    /**
     * Background of the QR code.
     * Can be [DrawableSource], [QrVectorColor] or both
     * */
    fun background(block: QrVectorBackgroundBuilderScope.() -> Unit)

    /**
     * Middle image.
     * */
    fun logo(block: QrVectorLogoBuilderScope.() -> Unit)

    /**
     * Highlight anchor QR code elements for better recognition.
     * Has the most impact when using a background image or color
     * */
    fun highlighting(block : QrHighlightingBuilderScope.() -> Unit)
}




