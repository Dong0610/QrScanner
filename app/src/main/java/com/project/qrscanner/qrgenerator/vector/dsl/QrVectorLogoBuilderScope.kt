package com.project.qrscanner.qrgenerator.vector.dsl

import android.graphics.drawable.Drawable
import com.project.qrscanner.qrgenerator.vector.style.*
import com.project.qrscanner.qrgenerator.style.BitmapScale

sealed interface QrVectorLogoBuilderScope : IQRVectorLogo {

    override var drawable: Drawable?
    override var size : Float
    override var padding : QrVectorLogoPadding
    override var shape: QrVectorLogoShape
    override var scale: BitmapScale
    override var backgroundColor : QrVectorColor
}

