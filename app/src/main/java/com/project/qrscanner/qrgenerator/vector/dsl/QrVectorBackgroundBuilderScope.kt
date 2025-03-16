package com.project.qrscanner.qrgenerator.vector.dsl

import android.graphics.drawable.Drawable
import com.project.qrscanner.qrgenerator.style.BitmapScale
import com.project.qrscanner.qrgenerator.vector.style.IQrVectorBackground
import com.project.qrscanner.qrgenerator.vector.style.QrVectorColor

sealed interface QrVectorBackgroundBuilderScope : IQrVectorBackground {

    override var drawable: Drawable?
    override var scale: BitmapScale
    override var color: QrVectorColor
}