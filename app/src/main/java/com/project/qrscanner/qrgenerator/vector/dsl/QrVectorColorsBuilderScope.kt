package com.project.qrscanner.qrgenerator.vector.dsl

import com.project.qrscanner.qrgenerator.vector.style.IQrVectorColors
import com.project.qrscanner.qrgenerator.vector.style.QrVectorColor


sealed interface QrVectorColorsBuilderScope : IQrVectorColors {
    override var ball: QrVectorColor
    override var dark: QrVectorColor
    override var frame: QrVectorColor
    override var light: QrVectorColor
}

