package com.project.qrscanner.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.LinearLayout
import com.dong.baselib.widget.dpToPx

class CustomBottomNavView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val barShape = BarShape(
        offset = context.resources.displayMetrics.widthPixels / 2f,
        circleRadius = 22f.dpToPx(),
        cornerRadius = 16f.dpToPx(),
        circleGap = 4f.dpToPx()
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    init {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_HARDWARE, null)
        setBackgroundColor(Color.TRANSPARENT)
       elevation=12f
    }

    override fun onDraw(canvas: Canvas) {
        val path = barShape.getPath(width.toFloat(), height.toFloat())
        canvas.drawPath(path, paint)
        super.onDraw(canvas)
    }

    class BarShape(
        private val offset: Float,
        private val circleRadius: Float,
        private val cornerRadius: Float,
        private val circleGap: Float
    ) {

        fun getPath(width: Float, height: Float): Path {
            val cutoutCenterX = offset
            val cutoutRadius = circleRadius + circleGap
            val cornerDiameter = cornerRadius * 2

            return Path().apply {
                val cutoutEdgeOffset = cutoutRadius * 1.8f
                val cutoutLeftX = cutoutCenterX - cutoutEdgeOffset
                val cutoutRightX = cutoutCenterX + cutoutEdgeOffset

                moveTo(0F, height)
                if (cornerRadius > 0) {
                    arcTo(
                        RectF(0f, 0f, cornerDiameter, cornerDiameter),
                        180f, 90f, false
                    )
                }

                lineTo(cutoutLeftX, 0f)
                cubicTo(
                    cutoutCenterX - cutoutRadius, 0f,
                    cutoutCenterX - cutoutRadius, cutoutRadius,
                    cutoutCenterX, cutoutRadius
                )
                cubicTo(
                    cutoutCenterX + cutoutRadius, cutoutRadius,
                    cutoutCenterX + cutoutRadius, 0f,
                    cutoutRightX, 0f
                )

                if (cornerRadius > 0) {
                    arcTo(
                        RectF(width - cornerDiameter, 0f, width, cornerDiameter),
                        -90f, 90f, false
                    )
                }

                lineTo(width, height)
                close()
            }
        }
    }
}
