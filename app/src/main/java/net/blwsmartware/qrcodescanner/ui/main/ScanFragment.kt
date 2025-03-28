package net.blwsmartware.qrcodescanner.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dong.baselib.file.getPathFromUri
import com.dong.baselib.widget.click
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.app.isSound
import net.blwsmartware.qrcodescanner.app.isVibrate
import net.blwsmartware.qrcodescanner.app.permission
import net.blwsmartware.qrcodescanner.app.toast
import net.blwsmartware.qrcodescanner.app.viewModel
import net.blwsmartware.qrcodescanner.base.BaseFragment
import net.blwsmartware.qrcodescanner.databinding.FragmentScanBinding
import net.blwsmartware.qrcodescanner.dialog.DialogRequestPermission
import net.blwsmartware.qrcodescanner.model.ScanModel
import net.blwsmartware.qrcodescanner.model.ScanType
import net.blwsmartware.qrcodescanner.ui.result.ResultScanActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max

@Suppress("DEPRECATION")
class ScanFragment : BaseFragment<FragmentScanBinding>(FragmentScanBinding::inflate, true) {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private val cameraSelector = MutableStateFlow(CameraSelector.DEFAULT_BACK_CAMERA)
    private var cameraJob: Job? = null
    private var code = ""


    companion object {
        private val isScanning = AtomicBoolean(true)
    }

    private var permissionDialog: DialogRequestPermission? = null

    private var isInitialized = false
    override fun FragmentScanBinding.initView() {
        root.setPadding(0, 0, 0, 40)

        isInitialized = true

        permissionDialog = DialogRequestPermission(requireActivity()) {
            fragmentAttach?.fragmentSendData("permission", "")
        }
    }

    override fun FragmentScanBinding.onClick() {
        ivChange.click {
            cameraSelector.update {
                if (it == CameraSelector.DEFAULT_BACK_CAMERA)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            }
        }

        ivSelectImage.click {
            resultSelectFileLauncher.launch("image/*")
        }
    }


    private fun setupCamera() {
        val imageCapture = ImageCapture.Builder()
            .setTargetResolution(Size(1080, 1920))
            .build()

        preview = Preview.Builder().build().also {
            it.surfaceProvider = binding.previewView.surfaceProvider
        }

        imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1080, 1920))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, QrcodeAnalyzer { result ->
                    if (isScanning.get() && code != result) {
                        code = result
                        isScanning.set(false)
                        captureImage(imageCapture) { bitmap, path ->
                            handleScanResult(result, bitmap, path)
                        }
                    }
                })
            }

        bindCamera(imageCapture)
    }

    private fun handleScanResult(code: String, bitmap: Bitmap, imagePath: String) {
        BarcodeScanning.getClient().process(InputImage.fromBitmap(bitmap, 0))
            .addOnSuccessListener { barcodes ->

                barcodes.firstOrNull()?.boundingBox?.let { box ->
                    val croppedBitmap = Bitmap.createBitmap(
                        bitmap, box.left, box.top, box.width(), box.height()
                    )
                    if (isVibrate) {
                        vibrate()
                    }
                    if (isSound) {
                        beep()
                    }

                    viewModel.scanResult.update {
                        ScanModel(code, croppedBitmap, imagePath, ScanType.QRCODE)
                    }
                    launchActivity<ResultScanActivity>()
                } ?: kotlin.run {
                    toast = getString(R.string.canot_get_qr_code)
                }
            }
    }


    fun scanQRCodeFromUri(uri: Uri, callback: (String, Int, Bitmap) -> Unit) {
        val inputStream = appContext.contentResolver.openInputStream(uri)
        inputStream?.use { stream ->
            val bitmap = BitmapFactory.decodeStream(stream)

            processImage(bitmap) { result, type, bitmap ->
                if (result != null && bitmap != null) {
                    callback(result, type ?: Barcode.FORMAT_QR_CODE, bitmap)
                } else {

                    toast = getString(R.string.canot_get_qr_code)
                }
            }
        }
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap): Bitmap {
        val maxDimension = 2048
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        if (originalWidth <= maxDimension && originalHeight <= maxDimension) {
            return bitmap
        }

        val ratio = maxDimension.toFloat() / max(originalWidth, originalHeight)
        val newWidth = (originalWidth * ratio).toInt()
        val newHeight = (originalHeight * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }


    private fun processImage(bitmap: Bitmap, callback: (String?, Int?, Bitmap?) -> Unit) {
        try {
            val scaledBitmap = scaleBitmapIfNeeded(bitmap)

            val image = InputImage.fromBitmap(scaledBitmap, 0)
            val scanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        val barcode = barcodes[0]
                        barcode.boundingBox?.let { box ->
                            try {
                                val left = box.left.coerceIn(0, scaledBitmap.width - 1)
                                val top = box.top.coerceIn(0, scaledBitmap.height - 1)
                                val width = box.width().coerceAtMost(scaledBitmap.width - left)
                                val height = box.height().coerceAtMost(scaledBitmap.height - top)
                                val croppedBitmap = Bitmap.createBitmap(
                                    scaledBitmap,
                                    left,
                                    top,
                                    width,
                                    height
                                )
                                callback(barcode.rawValue, barcode.format, croppedBitmap)
                            } catch (e: Exception) {
                                Log.e("QRScanner", "Error cropping bitmap", e)
                                callback(barcode.rawValue, barcode.format, scaledBitmap)
                            }
                        } ?: callback(barcode.rawValue, barcode.format, scaledBitmap)
                    } else {
                        callback(null, null, null)
                    }
                }
                .addOnFailureListener {
                    Log.e("QRScanner", "Failed to process image", it)
                    callback(null, null, null)
                }
        } catch (e: Exception) {
            Log.e("QRScanner", "Error in processImage", e)
            callback(null, null, null)
        }
    }

    private fun bindCamera(imageCapture: ImageCapture) {
        cameraJob?.cancel()
        cameraJob = lifecycleScope.launch {
            cameraSelector.collectLatest { selector ->
                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        viewLifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis,
                        imageCapture
                    )
                    setupFlashControl(camera)
                    zoomControl(camera)
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Binding failed", exc)
                }
            }
        }
    }

    private fun zoomControl(camera: Camera) {
        binding.ivZoomIn.setOnClickListener {
            val zoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 1f
            val maxZoom = camera.cameraInfo.zoomState.value?.maxZoomRatio ?: 1f
            camera.cameraControl.setZoomRatio((zoomRatio + 0.1f).coerceAtMost(maxZoom))
        }

        binding.ivZoomOut.setOnClickListener {
            val zoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 1f
            val minZoom = camera.cameraInfo.zoomState.value?.minZoomRatio ?: 1f
            camera.cameraControl.setZoomRatio((zoomRatio - 0.1f).coerceAtLeast(minZoom))
        }
    }

    private fun setupFlashControl(camera: Camera) {
        if (camera.cameraInfo.hasFlashUnit()) {
            binding.ivStateFlash.setOnClickListener {
                val flashOn = camera.cameraInfo.torchState.value != TorchState.ON
                camera.cameraControl.enableTorch(flashOn)
                binding.ivStateFlash.setImageResource(
                    if (flashOn) R.drawable.ic_flash_on else R.drawable.ic_flash_off
                )
            }
        }
    }

    private fun captureImage(imageCapture: ImageCapture, callback: (Bitmap, String) -> Unit) {
        val photoFile = File(
            appContext.externalMediaDirs.first(),
            "${System.currentTimeMillis()}.jpg"
        )
        imageCapture.takePicture(
            ImageCapture.OutputFileOptions.Builder(photoFile).build(),
            ContextCompat.getMainExecutor(appContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    callback(
                        BitmapFactory.decodeFile(photoFile.absolutePath),
                        photoFile.absolutePath
                    )
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraCapture", "Failed: ${exc.message}", exc)
                }
            }
        )
    }

    class QrcodeAnalyzer(private val onResult: (String) -> Unit) : ImageAnalysis.Analyzer {
        private val scanner = BarcodeScanning.getClient()
        private var isProcessing = AtomicBoolean(false)

        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            if (!isScanning.get() || isProcessing.get()) {
                imageProxy.close()
                return
            }

            imageProxy.image?.let {
                isProcessing.set(true)
                val inputImage = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)
                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        barcodes.firstOrNull()?.rawValue?.let(onResult)
                    }
                    .addOnCompleteListener {
                        isProcessing.set(false)
                        imageProxy.close()
                    }
            } ?: imageProxy.close()
        }
    }

    private var isShowCameraDialog = false
    override fun backPress() {
        super.backPress()
        fragmentAttach?.fragmentOnBack()
    }

    override fun onResume() {
        super.onResume()
        if (permissionDialog?.isShowing == true) {
            permissionDialog?.dismiss()
        }
        if (!permission.checkGrantedCamera && !isShowCameraDialog) {
            isShowCameraDialog = true
            permissionDialog?.show()
        }
        isScanning.set(true)
        code = ""
        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()
    }

    private fun restartCamera() {
        try {
            isScanning.set(true)
            code = ""
            if (::cameraProvider.isInitialized) {
                startCamera()
            }
        } catch (e: Exception) {
            Log.e("Camera", "Failed to restart camera", e)
        }
    }

    private val resultSelectFileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { result ->

        result?.let {
            scanQRCodeFromUri(it) { code, type, bitpmap ->
                if (isVibrate) {
                    vibrate()
                }
                if (isSound) {
                    beep()
                }


                getPathFromUri(appContext, it)?.let { fileInfo ->
                    viewModel.scanResult.update {
                        ScanModel(
                            code,
                            bitpmap,
                            fileInfo ?: "",
                            ScanType.QRCODE
                        )
                    }
                    launchActivity<ResultScanActivity>()
                }
                    ?:run {
                        Toast.makeText(appContext, getString(R.string.cant_get_file_path), Toast.LENGTH_SHORT).show()
                    }

            }

        }

    }

    private fun vibrate() {
        val vibrator = appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect =
                    VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
                it.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(200)
            }
        }
    }

    private fun beep() {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
    }

    private fun startCamera() {
        ProcessCameraProvider.getInstance(appContext).apply {
            addListener({
                try {
                    cameraProvider = get()
                    setupCamera()
                } catch (e: Exception) {
                    Log.e("Camera", "Failed to get camera provider", e)
                }
            }, ContextCompat.getMainExecutor(appContext))
        }
    }


    override fun onPause() {
        super.onPause()
        cleanup()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cleanup()
    }

    private fun cleanup() {
        cameraJob?.cancel()
        cameraJob = null
        if (::cameraProvider.isInitialized) {
            try {
                cameraProvider.unbindAll()
            } catch (exc: Exception) {
                Log.e("CameraPreview", "Cleanup failed", exc)
            }
        }
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }
}