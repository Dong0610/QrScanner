package net.blwsmartware.qrcodescanner.qrgenerator.vector.dsl

import net.blwsmartware.qrcodescanner.qrgenerator.vector.QrVectorOptions
import net.blwsmartware.qrcodescanner.qrgenerator.vector.style.QrVectorColor

internal class InternalQrVectorColorsBuilderScope(
    private val builder: QrVectorOptions.Builder
) :  QrVectorColorsBuilderScope {
    override var dark: QrVectorColor
        get() = builder.colors.dark
        set(value) = with(builder){
            setColors(colors.copy(
                dark = value
            ))
        }

    override var light: QrVectorColor
        get() = builder.colors.light
        set(value) = with(builder){
            setColors(colors.copy(
                light = value
            ))
        }

    override var ball: QrVectorColor
        get() = builder.colors.ball
        set(value) = with(builder){
            setColors(colors.copy(
                ball = value
            ))
        }
    override var frame: QrVectorColor
        get() = builder.colors.frame
        set(value) = with(builder){
            setColors(colors.copy(
                frame = value
            ))
        }
}