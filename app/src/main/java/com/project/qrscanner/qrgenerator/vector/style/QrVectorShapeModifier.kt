package com.project.qrscanner.qrgenerator.vector.style

import android.graphics.Path
import com.project.qrscanner.qrgenerator.style.Neighbors

fun interface QrVectorShapeModifier {

    fun createPath(size : Float, neighbors: Neighbors) : Path
}