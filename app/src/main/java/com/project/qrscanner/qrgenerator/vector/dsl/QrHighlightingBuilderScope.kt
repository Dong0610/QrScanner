package com.project.qrscanner.qrgenerator.vector.dsl

import com.project.qrscanner.qrgenerator.HighlightingType
import com.project.qrscanner.qrgenerator.IAnchorsHighlighting

sealed interface QrHighlightingBuilderScope : IAnchorsHighlighting {
    override var cornerEyes: HighlightingType
    override var versionEyes: HighlightingType
    override var timingLines: HighlightingType
    override val alpha: Float
}