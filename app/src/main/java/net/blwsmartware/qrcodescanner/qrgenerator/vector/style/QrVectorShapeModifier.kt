package net.blwsmartware.qrcodescanner.qrgenerator.vector.style

import android.graphics.Path
import net.blwsmartware.qrcodescanner.qrgenerator.style.Neighbors

fun interface QrVectorShapeModifier {

    fun createPath(size : Float, neighbors: Neighbors) : Path
}