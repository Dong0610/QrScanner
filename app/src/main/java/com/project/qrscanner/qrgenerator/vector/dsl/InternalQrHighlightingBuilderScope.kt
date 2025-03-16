package com.project.qrscanner.qrgenerator.vector.dsl

import com.project.qrscanner.qrgenerator.HighlightingType
import com.project.qrscanner.qrgenerator.vector.QrVectorOptions

internal class InternalQrHighlightingBuilderScope(
    private val builder : QrVectorOptions.Builder
) : QrHighlightingBuilderScope {
    override var cornerEyes: HighlightingType
        get() = builder.highlighting.cornerEyes
        set(value) {
            builder.setAnchorsHighlighting(
                builder.highlighting.copy(
                    cornerEyes = value
                )
            )
        }

    override var versionEyes: HighlightingType
        get() = builder.highlighting.versionEyes
        set(value) {
            builder.setAnchorsHighlighting(
                builder.highlighting.copy(
                    versionEyes = value
                )
            )
        }

    override var timingLines: HighlightingType
        get() = builder.highlighting.timingLines
        set(value) {
            builder.setAnchorsHighlighting(
                builder.highlighting.copy(
                    timingLines = value
                )
            )
        }

    override var alpha: Float
        get() = builder.highlighting.alpha
        set(value) {
            builder.setAnchorsHighlighting(
                builder.highlighting.copy(
                    alpha = value
                )
            )
        }
}