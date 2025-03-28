@file:Suppress("DEPRECATION")

package net.blwsmartware.qrcodescanner.qrgenerator.dsl

import android.graphics.drawable.Drawable
import net.blwsmartware.qrcodescanner.qrgenerator.QrOptions
import net.blwsmartware.qrcodescanner.qrgenerator.style.*
import net.blwsmartware.qrcodescanner.qrgenerator.style.BitmapScale

sealed interface QrBackgroundBuilderScope : IQRBackground {

    override var drawable: Drawable?

    override var alpha : Float

    override var scale: BitmapScale

    override var color : QrColor
}

class InternalQrBackgroundBuilderScope internal constructor(
    val builder: QrOptions.Builder
) : QrBackgroundBuilderScope {

    override var drawable: Drawable?
        get() = builder.background.drawable
        set(value) = with(builder) {
            background(background.copy(drawable = value))
        }

    override var alpha: Float
        get() = builder.background.alpha
        set(value) = with(builder) {
            background(background.copy(alpha = value))
        }

    override var scale: BitmapScale
        get() = builder.background.scale
        set(value) = with(builder) {
            background(background.copy(scale = value))
        }

    override var color: QrColor
        get() = builder.background.color
        set(value) = with(builder) {
            background(background.copy(color = value))
        }
}