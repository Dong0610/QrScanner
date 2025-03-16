import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class QrResult {
    data class Success(val bitmap: Bitmap) : QrResult()
    data class Error(val message: String) : QrResult()
}

object QrGenerator {
    fun generateQrZXing(
        content: String,
        width: Int = 512,
        height: Int = 512,
        margin: Int = 1,
        color: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): QrResult {
        return try {
            val hints = hashMapOf<EncodeHintType, Any>().apply {
                put(EncodeHintType.MARGIN, margin)
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            }

            val writer = MultiFormatWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) color else backgroundColor)
                }
            }

            QrResult.Success(bitmap)
        } catch (e: Exception) {
            QrResult.Error("Failed to generate QR code: ${e.message}")
        }
    }



    fun generateQrCustomStyle(
        content: String,
        width: Int = 1024,
        height: Int = 1024,
        qrStyle: QrStyle = QrStyle()
    ): QrResult {
        return try {
            val hints = hashMapOf<EncodeHintType, Any>().apply {
                put(EncodeHintType.MARGIN, qrStyle.margin)
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            }

            val writer = MultiFormatWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            if (qrStyle.backgroundColor != Color.TRANSPARENT) {
                canvas.drawColor(qrStyle.backgroundColor)
            }

            val paint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
            }

            val cellWidth = width / bitMatrix.width.toFloat()
            val cellHeight = height / bitMatrix.height.toFloat()

            for (x in 0 until bitMatrix.width) {
                for (y in 0 until bitMatrix.height) {
                    if (bitMatrix[x, y]) {
                        when (qrStyle.dotStyle) {
                            DotStyle.SQUARE -> {
                                paint.color = qrStyle.dotColor
                                canvas.drawRect(
                                    x * cellWidth,
                                    y * cellHeight,
                                    (x + 1) * cellWidth,
                                    (y + 1) * cellHeight,
                                    paint
                                )
                            }
                            DotStyle.CIRCLE -> {
                                paint.color = qrStyle.dotColor
                                canvas.drawCircle(
                                    (x + 0.5f) * cellWidth,
                                    (y + 0.5f) * cellHeight,
                                    cellWidth / 2,
                                    paint
                                )
                            }
                            DotStyle.ROUNDED_SQUARE -> {
                                paint.color = qrStyle.dotColor
                                canvas.drawRoundRect(
                                    x * cellWidth,
                                    y * cellHeight,
                                    (x + 1) * cellWidth,
                                    (y + 1) * cellHeight,
                                    cellWidth / 4,
                                    cellHeight / 4,
                                    paint
                                )
                            }
                        }
                    }
                }
            }

            if (qrStyle.logo != null) {
                val logoSize = minOf(width, height) / 4
                val logoLeft = (width - logoSize) / 2f
                val logoTop = (height - logoSize) / 2f
                
                val logoRect = RectF(logoLeft, logoTop, logoLeft + logoSize, logoTop + logoSize)
                
                if (qrStyle.logoBackgroundColor != Color.TRANSPARENT) {
                    paint.color = qrStyle.logoBackgroundColor
                    canvas.drawRect(logoRect, paint)
                }
                
                canvas.drawBitmap(
                    qrStyle.logo,
                    null,
                    logoRect,
                    paint
                )
            }

            QrResult.Success(bitmap)
        } catch (e: Exception) {
            QrResult.Error("Failed to generate QR code: ${e.message}")
        }
    }
}

data class QrStyle(
    val dotColor: Int = Color.BLACK,
    val backgroundColor: Int = Color.WHITE,
    val margin: Int = 6,
    val dotStyle: DotStyle = DotStyle.SQUARE,
    val logo: Bitmap? = null,
    val logoBackgroundColor: Int = Color.WHITE
)

enum class DotStyle {
    SQUARE, CIRCLE, ROUNDED_SQUARE
} 