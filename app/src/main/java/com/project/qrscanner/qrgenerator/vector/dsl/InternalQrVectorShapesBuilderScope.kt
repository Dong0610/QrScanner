package com.project.qrscanner.qrgenerator.vector.dsl

import com.project.qrscanner.qrgenerator.vector.QrVectorOptions
import com.project.qrscanner.qrgenerator.vector.style.QrVectorBallShape
import com.project.qrscanner.qrgenerator.vector.style.QrVectorFrameShape
import com.project.qrscanner.qrgenerator.vector.style.QrVectorPixelShape


internal class InternalQrVectorShapesBuilderScope(
    private val builder: QrVectorOptions.Builder,
    override val centralSymmetry : Boolean,
) : QrVectorShapesBuilderScope {

    init {
        builder.setShapes(builder.shapes.copy(
            centralSymmetry = centralSymmetry
        ))
    }

    override var darkPixel: QrVectorPixelShape
        get() = builder.shapes.darkPixel
        set(value) = with(builder){
            setShapes(shapes.copy(
                darkPixel = value
            ))
        }

    override var lightPixel: QrVectorPixelShape
        get() = builder.shapes.lightPixel
        set(value) = with(builder){
            setShapes(shapes.copy(
                lightPixel = value
            ))
        }

    override var ball: QrVectorBallShape
        get() = builder.shapes.ball
        set(value) = with(builder){
            setShapes(shapes.copy(
                ball = value
            ))
        }

    override var frame: QrVectorFrameShape
        get() = builder.shapes.frame
        set(value) = with(builder){
            setShapes(shapes.copy(
                frame = value
            ))
        }
}