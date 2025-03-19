package net.blwsmartware.qrcodescanner.qrgenerator.vector.dsl

import net.blwsmartware.qrcodescanner.qrgenerator.vector.style.IQrVectorShapes
import net.blwsmartware.qrcodescanner.qrgenerator.vector.style.QrVectorBallShape
import net.blwsmartware.qrcodescanner.qrgenerator.vector.style.QrVectorFrameShape
import net.blwsmartware.qrcodescanner.qrgenerator.vector.style.QrVectorPixelShape

sealed interface QrVectorShapesBuilderScope : IQrVectorShapes {
    override var darkPixel: QrVectorPixelShape
    override var lightPixel: QrVectorPixelShape
    override var ball: QrVectorBallShape
    override var frame: QrVectorFrameShape
}

