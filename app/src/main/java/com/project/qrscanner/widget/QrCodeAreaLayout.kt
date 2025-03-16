package com.project.qrscanner.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class QrCodeAreaLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var scannerArea: Rect = Rect()
    private val overlayPaint = Paint().apply {
        color = Color.BLACK
        alpha = 150 // 60% transparency
    }

    private val cornerPaint = Paint().apply {
        color = ContextCompat.getColor(context, com.dong.baselib.R.color.Blue_Lotus)
        strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val canvasWidth = canvas.width.toFloat()
        val canvasHeight = canvas.height.toFloat()
        val boxSize = minOf(canvasWidth, canvasHeight) * 0.65f
        val left = (canvasWidth - boxSize) / 2
        val top = (canvasHeight - boxSize) / 2
        scannerArea.set(
            left.toInt(),
            top.toInt(),
            (left + boxSize).toInt(),
            (top + boxSize).toInt()
        )
        canvas.drawRect(0f, 0f, canvasWidth, top, overlayPaint)

        canvas.drawRect(0f, top, left, top + boxSize, overlayPaint)
        canvas.drawRect(left + boxSize, top, canvasWidth, top + boxSize, overlayPaint)
        // Bottom
        canvas.drawRect(0f, top + boxSize, canvasWidth, canvasHeight, overlayPaint)

        // Draw corner rectangles (for scanner box)
        val cornerSizeW = boxSize * 0.2f
        val cornerSizeH = boxSize * 0.03f

        // Top-left corner
        canvas.drawRect(left, top, left + cornerSizeW, top + cornerSizeH, cornerPaint)
        canvas.drawRect(left, top, left + cornerSizeH, top + cornerSizeW, cornerPaint)

        // Top-right corner
        canvas.drawRect(left + boxSize - cornerSizeW, top, left + boxSize, top + cornerSizeH, cornerPaint)
        canvas.drawRect(left + boxSize - cornerSizeH, top, left + boxSize, top + cornerSizeW, cornerPaint)

        // Bottom-left corner
        canvas.drawRect(left, top + boxSize - cornerSizeH, left + cornerSizeW, top + boxSize, cornerPaint)
        canvas.drawRect(left, top + boxSize - cornerSizeW, left + cornerSizeH, top + boxSize, cornerPaint)

        // Bottom-right corner
        canvas.drawRect(left + boxSize - cornerSizeW, top + boxSize - cornerSizeH, left + boxSize, top + boxSize, cornerPaint)
        canvas.drawRect(left + boxSize - cornerSizeH, top + boxSize - cornerSizeW, left + boxSize, top + boxSize, cornerPaint)
    }
}
