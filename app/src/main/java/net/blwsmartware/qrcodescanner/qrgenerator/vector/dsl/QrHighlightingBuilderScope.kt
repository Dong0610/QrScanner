package net.blwsmartware.qrcodescanner.qrgenerator.vector.dsl

import net.blwsmartware.qrcodescanner.qrgenerator.HighlightingType
import net.blwsmartware.qrcodescanner.qrgenerator.IAnchorsHighlighting

sealed interface QrHighlightingBuilderScope : IAnchorsHighlighting {
    override var cornerEyes: HighlightingType
    override var versionEyes: HighlightingType
    override var timingLines: HighlightingType
    override val alpha: Float
}