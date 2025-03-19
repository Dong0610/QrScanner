package net.blwsmartware.qrcodescanner.qrgenerator.vector.dsl

import android.graphics.drawable.Drawable
import net.blwsmartware.qrcodescanner.qrgenerator.style.BitmapScale
import net.blwsmartware.qrcodescanner.qrgenerator.vector.QrVectorOptions
import net.blwsmartware.qrcodescanner.qrgenerator.vector.style.QrVectorColor

internal class InternalQrVectorBackgroundBuilderScope(
    val builder: QrVectorOptions.Builder
) : QrVectorBackgroundBuilderScope {

    override var drawable: Drawable?
        get() = builder.background.drawable
        set(value) = with(builder){
            setBackground(background.copy(drawable = value))
        }
    override var scale: BitmapScale
        get() = builder.background.scale
        set(value) = with(builder){
            setBackground(background.copy(scale = value))
        }
    override var color: QrVectorColor
        get() = builder.background.color
        set(value) = with(builder) {
            setBackground(background.copy(color = value))
        }
}