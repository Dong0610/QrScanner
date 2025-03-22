package net.blwsmartware.qrcodescanner.scanner

import androidx.camera.core.ImageAnalysis
import com.google.mlkit.vision.barcode.BarcodeScanning
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
class QrcodeAnalyzer(private val barcodeListener: ScannerListener) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { mediaImage ->
            processImage(imageProxy, mediaImage)
        } ?: imageProxy.close()
    }

    private fun processImage(imageProxy: ImageProxy, mediaImage: Image) {
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val rawBitmap = imageProxy.toBitmap()
        val rotatedBitmap = rotateBitmap(rawBitmap, rotationDegrees)
        val inputImage = InputImage.fromBitmap(rotatedBitmap, 0)
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val barcode = barcodes.firstOrNull()
                if (barcode != null) {
                    barcode.boundingBox?.let { bounds ->
                        barcodeListener.onScanResult(barcode.rawValue ?: "")
                    }
                }
            }
            .addOnFailureListener { Log.e("QRDetection", "ML Kit QR detection failed", it) }
            .addOnCompleteListener { imageProxy.close() }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        if (degrees == 0) return bitmap
        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
