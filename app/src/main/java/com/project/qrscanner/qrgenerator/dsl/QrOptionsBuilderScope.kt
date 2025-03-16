@file:Suppress("DEPRECATION")

package com.project.qrscanner.qrgenerator.dsl

import com.project.qrscanner.qrgenerator.QrErrorCorrectionLevel
import com.project.qrscanner.qrgenerator.QrOptions
import com.project.qrscanner.qrgenerator.createQrOptions
import com.project.qrscanner.qrgenerator.style.*

sealed interface QrOptionsBuilderScope {

    var shape : QrShape
    
    val padding : Float

    val width : Int

    val height : Int

    var errorCorrectionLevel : QrErrorCorrectionLevel
    fun offset(block : QrOffsetBuilderScope.() -> Unit)

    fun logo(block : QrLogoBuilderScope.() -> Unit)

    fun background(block: QrBackgroundBuilderScope.() -> Unit)

    fun colors(block : QrColorsBuilderScope.() -> Unit)
    fun shapes(block : QrElementsShapesBuilderScope.() -> Unit)
}



fun QrOptionsBuilderScope(builder: QrOptions.Builder) : QrOptionsBuilderScope =
    InternalQrOptionsBuilderScope(builder)



private class InternalQrOptionsBuilderScope(
    private val builder: QrOptions.Builder
) : QrOptionsBuilderScope {

    override fun offset(block: QrOffsetBuilderScope.() -> Unit) {
        InternalQrOffsetBuilderScope(builder).apply(block)
    }

    override fun logo(block: QrLogoBuilderScope.() -> Unit) {
        InternalQrLogoBuilderScope(
            builder,
            width = builder.width,
            height = builder.height
        ).apply(block)
    }

    override fun background(block: QrBackgroundBuilderScope.() -> Unit) {
        InternalQrBackgroundBuilderScope(builder).apply(block)
    }

    override fun colors(block: QrColorsBuilderScope.() -> Unit) {
        InternalColorsBuilderScope(builder).apply(block)
    }

    override fun shapes(block: QrElementsShapesBuilderScope.() -> Unit) {
        InternalQrElementsShapesBuilderScope(builder).apply(block)
    }

    override var shape: QrShape
        get() = builder.codeShape
        set(value) {
            builder.codeShape(value)
        }

    override val padding: Float
        get() = builder.padding

    override val width: Int by builder::width

    override val height: Int by builder::height

    override var errorCorrectionLevel: QrErrorCorrectionLevel
        get() = builder.errorCorrectionLevel
        set(value) {
            builder.errorCorrectionLevel(value)
        }
}