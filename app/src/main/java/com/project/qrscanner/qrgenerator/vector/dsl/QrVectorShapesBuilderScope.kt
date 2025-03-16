package com.project.qrscanner.qrgenerator.vector.dsl

import com.project.qrscanner.qrgenerator.vector.style.IQrVectorShapes
import com.project.qrscanner.qrgenerator.vector.style.QrVectorBallShape
import com.project.qrscanner.qrgenerator.vector.style.QrVectorFrameShape
import com.project.qrscanner.qrgenerator.vector.style.QrVectorPixelShape

sealed interface QrVectorShapesBuilderScope : IQrVectorShapes {
    override var darkPixel: QrVectorPixelShape
    override var lightPixel: QrVectorPixelShape
    override var ball: QrVectorBallShape
    override var frame: QrVectorFrameShape
}

