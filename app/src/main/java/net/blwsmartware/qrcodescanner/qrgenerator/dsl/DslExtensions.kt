@file:Suppress("DEPRECATION")

package net.blwsmartware.qrcodescanner.qrgenerator.dsl

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import net.blwsmartware.qrcodescanner.qrgenerator.style.*
import kotlin.math.roundToInt
import kotlin.reflect.KClass
inline fun QrColorsBuilderScope.draw(
    crossinline action : Canvas.() -> Unit
) : QrColor = QrCanvasColor { canvas -> action(canvas) }
    .let {
        val (width, height)=when(this){
            is InternalColorsBuilderScope -> builder.width to builder.height
        }
        it.toQrColor(width, height)
    }

inline fun QrBackgroundBuilderScope.draw(
    crossinline action : Canvas.() -> Unit
) : QrColor = QrCanvasColor { action(it) }
    .let {
        val (width, height) = when(this){
            is InternalQrBackgroundBuilderScope -> builder.width to builder.height
        }
        it.toQrColor(width, height)
    }

enum class Efficiency {
    Memory,
    Time
}

inline fun <reified T : QrShapeModifier> QrElementsShapesBuilderScope.pathShape(
    efficiency: Efficiency = Efficiency.Time,
    noinline builder: Path.(size: Int) -> Unit
): T = pathShape(T::class, efficiency, builder)
@Suppress("unchecked_cast")
fun <T : QrShapeModifier> QrElementsShapesBuilderScope.pathShape(
    clazz: KClass<T>,
    efficiency: Efficiency = Efficiency.Time,
    builder: Path.(size: Int) -> Unit
) : T = when(efficiency) {
    Efficiency.Time -> drawShape(clazz) { drawPaint, _ ->
        drawPath(Path().apply { builder(minOf(width, height)) }, drawPaint)
    }
    Efficiency.Memory -> QrShapeModifierFromPath(builder)
        .toTypedShapeModifier(clazz)
}

inline fun <reified T : QrShapeModifier> QrLogoBuilderScope.pathShape(
    efficiency: Efficiency = Efficiency.Time,
    noinline builder: Path.(size: Int) -> Unit
): T = pathShape(T::class, efficiency, builder)

fun <T : QrShapeModifier> QrLogoBuilderScope.pathShape(
    clazz: KClass<T>,
    efficiency: Efficiency = Efficiency.Time,
    builder: Path.(size: Int) -> Unit
) : T = when(efficiency) {
    Efficiency.Time -> drawShape(clazz) { drawPaint, _ ->
        drawPath(Path().apply { builder(minOf(width, height)) }, drawPaint)
    }
    Efficiency.Memory -> QrShapeModifierFromPath(builder)
        .toTypedShapeModifier(clazz)
}
@Deprecated(
    "Use pathShape instead",
    ReplaceWith("pathShape"),
    level = DeprecationLevel.WARNING
)
inline fun <reified T : QrShapeModifier> QrElementsShapesBuilderScope.drawShape(
    noinline draw : Canvas.(drawPaint : Paint, erasePaint : Paint) -> Unit
): T = drawShape(T::class, draw)

@Deprecated(
    "Use pathShape instead",
    ReplaceWith("pathShape"),
    level = DeprecationLevel.WARNING
)
@Suppress("deprecation")
inline fun <reified T : QrShapeModifier> QrLogoBuilderScope.drawShape(
    noinline draw : Canvas.(drawPaint : Paint, erasePaint : Paint) -> Unit
): T = drawShape(T::class, draw)

@Suppress("unchecked_cast")
@Deprecated(
    "Use pathShape instead",
    ReplaceWith("pathShape"),
    level = DeprecationLevel.WARNING
)
fun <T : QrShapeModifier> QrElementsShapesBuilderScope.drawShape(
    clazz: KClass<T>,
    draw : Canvas.(drawPaint : Paint, erasePaint : Paint) -> Unit
) : T = QrCanvasShape(draw)
    .let {
        val (size, padding) = when (this) {
            is InternalQrElementsShapesBuilderScope ->
                minOf(builder.width, builder.height) to builder.padding
        }
        it.toTypedShapeModifier(clazz, size, padding)
    }

@Deprecated(
    "Use pathShape instead",
    ReplaceWith("pathShape"),
    level = DeprecationLevel.WARNING
)
@Suppress("unchecked_cast")
fun <T : QrShapeModifier> QrLogoBuilderScope.drawShape(
    clazz: KClass<T>,
    draw : Canvas.(drawPaint : Paint, erasePaint : Paint) -> Unit
) : T = QrCanvasShape(draw)
    .let {
        val (size, padding) = when (this) {
            is InternalQrLogoBuilderScope ->
                minOf(width, height) to codePadding
        }
        it.toTypedShapeModifier(clazz, size, padding)
    }

@Suppress("unchecked_cast")
private fun <T : QrShapeModifier> QrShapeModifier.toTypedShapeModifier(
    clazz: KClass<T>
) :T = when(clazz){
    QrPixelShape::class -> asPixelShape()
    QrBallShape::class -> asBallShape()
    QrFrameShape::class -> asFrameShape()
    QrLogoShape::class -> asLogoShape()
    QrHighlightingShape::class -> asHighlightingShape()
    else -> throw IllegalStateException(
        "Only QrElementsShapes properties and QrLogoShape can be smart casted"
    )
} as T

@Suppress("unchecked_cast")
private fun <T : QrShapeModifier> QrCanvasShape.toTypedShapeModifier(
    clazz: KClass<T>,
    size: Int,
    padding : Float,
) : T {
    val mul = when(clazz){
        QrPixelShape::class -> 21
        QrBallShape::class -> 7
        QrFrameShape::class, QrLogoShape::class -> 3
        else -> 1
    }
    return toShapeModifier((size * (1 - padding) / mul).roundToInt())
        .toTypedShapeModifier(clazz)
}
