package net.blwsmartware.qrcodescanner.qrgenerator.style

import net.blwsmartware.qrcodescanner.qrgenerator.style.Neighbors.Companion.Empty

/**
 * Calculate object shape using pixel-by-pixel decision
 * */
fun interface QrShapeModifier {
    /**
     * Calculate object shape using pixel-by-pixel decision
     *
     * @param i current row of [elementSize] x [elementSize] matrix
     * @param j current column [elementSize] x [elementSize] matrix
     * @param elementSize size of the element in pixels.
     * @param neighbors status of the neighbor qr-code pixels.
     * It will be non-[Empty] only for [QrPixelShape]
     * */
    operator fun invoke(
        i: Int, j: Int, elementSize: Int, neighbors: Neighbors
    ): Boolean
}
