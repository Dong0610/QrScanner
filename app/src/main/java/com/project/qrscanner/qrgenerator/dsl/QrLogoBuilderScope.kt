@file:Suppress("DEPRECATION")

package com.project.qrscanner.qrgenerator.dsl

import android.graphics.drawable.Drawable
import com.project.qrscanner.qrgenerator.QrOptions
import com.project.qrscanner.qrgenerator.style.*
import com.project.qrscanner.qrgenerator.style.BitmapScale

sealed interface QrLogoBuilderScope : IQRLogo {

    override var drawable: Drawable?
    override var size : Float
    override var padding : QrLogoPadding
    override var shape: QrLogoShape
    override var scale: BitmapScale
    override var backgroundColor : QrColor
}

internal class InternalQrLogoBuilderScope(
     val builder: QrOptions.Builder,
     val width : Int,
     val height : Int,
     val codePadding : Float = -1f
) : QrLogoBuilderScope {

    override var drawable: Drawable?
        get() = builder.logo.drawable
        set(value) = with(builder) {
            logo = logo.copy(drawable = value)
        }
    override var size: Float
        get() = builder.logo.size
        set(value) = with(builder) {
            logo = logo.copy(size = value)
        }

    override var padding: QrLogoPadding
        get() = builder.logo.padding
        set(value) = with(builder) {
            logo =logo.copy(padding = value)
        }
    override var shape: QrLogoShape
        get() = builder.logo.shape
        set(value) = with(builder) {
            logo = logo.copy(shape = value)
        }

    override var scale: BitmapScale
        get() = builder.logo.scale
        set(value) = with(builder) {
            logo = logo.copy(scale = value)
        }
    override var backgroundColor: QrColor
        get() = builder.logo.backgroundColor
        set(value) = with(builder) {
            logo = logo.copy(backgroundColor = value)
        }
}