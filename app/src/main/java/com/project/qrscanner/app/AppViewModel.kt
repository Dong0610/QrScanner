package com.project.qrscanner.app


import DotStyle
import QrGenerator
import QrResult
import QrStyle
import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.util.LruCache
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dong.baselib.file.saveImageToInternalStorage
import com.project.qrscanner.R
import com.project.qrscanner.database.AppDatabase
import com.project.qrscanner.database.AppDbRepository
import com.project.qrscanner.database.model.HistoryApp
import com.project.qrscanner.model.CreateType
import com.project.qrscanner.model.QrType
import com.project.qrscanner.model.ScanModel
import com.project.qrscanner.model.ScanType
import com.project.qrscanner.qrgenerator.QrCodeGenerator
import com.project.qrscanner.qrgenerator.QrData
import com.project.qrscanner.qrgenerator.createQrOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class AppViewModel(private val app: Application) : AndroidViewModel(app) {
    var currentIndexNav = MutableLiveData<Int>(R.id.homeFragment)
    val dataImpl get() = AppDbRepository(AppDatabase.init(app.applicationContext).history())
    var scanResult = MutableStateFlow<ScanModel?>(null)
    var bitmapQrCode = MutableStateFlow<Bitmap?>(null)
    val createHistory = MutableStateFlow<HistoryApp?>(null)

    private val qrCache = LruCache<String, Bitmap>(20)
    private val qrScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val options by lazy {
        createQrOptions(1024, 1024, 0f) {}
    }

    private val generator = QrCodeGenerator().apply {
        qrScope.launch {
            generateQrCodeSuspend(
                QrData.Text("warmup"),
                options,
                charset = Charsets.UTF_8
            )
        }
    }

    fun createQrWithValue(
        contentCreated: String,
        qrType: QrType = QrType.TEXT,
        callback: (Bitmap) -> Unit
    ) {
        viewModelScope.launch {
//            try {
//                qrCache.get(contentCreated)?.let {
//                    callback(it)
//                    saveQrToDb(contentCreated, it, qrType, "")
//                    return@launch
//                }
//
//                val bitmap = withContext(Dispatchers.Default) {
//                    generator.generateQrCodeSuspend(
//                        QrData.Text(contentCreated),
//                        options,
//                        charset = Charsets.UTF_8
//                    ).also {
//                        qrCache.put(contentCreated, it)
//                    }
//                }
//
//                callback(bitmap)
//                saveQrToDb(contentCreated, bitmap, qrType, "")
//            } catch (e: Exception) {
//                Log.e("AppViewModel", "Error generating QR code", e)
//            }

            val style = QrStyle(
                dotColor = Color.BLUE,
                backgroundColor = Color.WHITE,
                margin = 6,
                dotStyle = DotStyle.ROUNDED_SQUARE,
                logoBackgroundColor = Color.WHITE
            )
            val result = QrGenerator.generateQrCustomStyle(contentCreated, qrStyle = style)
            when (result) {
                is QrResult.Success -> {
                    callback(result.bitmap)
                    saveQrToDb(contentCreated, result.bitmap, qrType, "")
                }

                is QrResult.Error -> {
                    Toast.makeText(app, result.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}

            }
        }
    }

    fun createMultipleQrCodes(
        contents: List<String>,
        qrType: QrType = QrType.TEXT,
        onProgress: (Int, Int) -> Unit = { _, _ -> },
        onComplete: (List<Bitmap>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val results = contents.mapIndexed { index, content ->
                    val bitmap = qrCache.get(content) ?: withContext(Dispatchers.Default) {
                        generator.generateQrCodeSuspend(
                            QrData.Text(content),
                            options,
                            charset = Charsets.UTF_8
                        ).also {
                            qrCache.put(content, it)
                        }
                    }
                    onProgress(index + 1, contents.size)
                    bitmap
                }
                onComplete(results)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error generating multiple QR codes", e)
            }
        }
    }

    fun preloadQrCodes(contents: List<String>) {
        qrScope.launch {
            contents.forEach { content ->
                if (qrCache.get(content) == null) {
                    try {
                        val bitmap = generator.generateQrCodeSuspend(
                            QrData.Text(content),
                            options,
                            charset = Charsets.UTF_8
                        )
                        qrCache.put(content, bitmap)
                    } catch (e: Exception) {
                        Log.e("AppViewModel", "Error preloading QR code", e)
                    }
                }
            }
        }
    }

    suspend fun saveQrScanId(historyModel: HistoryApp): Long {
        return withContext(Dispatchers.IO) {
            dataImpl.insertWithId(historyModel)
        }
    }

    fun saveQrToDb(
        contentCreated: String,
        bitmapQrCode: Bitmap?,
        qrType: QrType,
        name: String,
        typeCreate: CreateType = CreateType.CREATE,
        callback: (HistoryApp) -> Unit = {},
    ) {
        viewModelScope.launch {
            val fileName = "Qrcode_${System.currentTimeMillis()}"
            if (bitmapQrCode != null) {
                val savedFilePath = saveImageToInternalStorage(app, bitmapQrCode, fileName)
                savedFilePath?.let {
                    val historyScan = HistoryApp().apply {
                        scanTime = System.currentTimeMillis().toString()
                        scanType = ScanType.QRCODE
                        scanVales = contentCreated
                        scanImage = it
                        qrName = name
                        type = qrType
                        createType = typeCreate
                    }
                    val idHis = saveQrScanId(historyScan)
                    withContext(Dispatchers.Main) {
                        callback(historyScan.apply { hisId = idHis.toInt() })
                        createHistory.update { historyScan }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        qrCache.evictAll()
        qrScope.cancel()
    }
}












