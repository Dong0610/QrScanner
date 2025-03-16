@file:Suppress("DEPRECATION")

package com.project.qrscanner.qrgenerator.dsl

import com.project.qrscanner.qrgenerator.QrOptions
import com.project.qrscanner.qrgenerator.style.IQrOffset
import com.project.qrscanner.qrgenerator.style.QrOffset

/**
 * @see QrOffset
 * */
sealed interface QrOffsetBuilderScope : IQrOffset {
    override var x: Float
    override var y: Float
}

internal class InternalQrOffsetBuilderScope(
    private val builder: QrOptions.Builder
) : QrOffsetBuilderScope {

    override var x: Float
        get() = builder.offset.x
        set(value) = with(builder) {
            offset = offset.copy(x = value)
        }

    override var y: Float
        get() = builder.offset.y
        set(value) = with(builder) {
            offset = offset.copy(y = value)
        }
}
