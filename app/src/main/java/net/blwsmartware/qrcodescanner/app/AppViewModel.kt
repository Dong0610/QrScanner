package net.blwsmartware.qrcodescanner.app


import BarcodeType
import DotStyle
import QrGenerator
import QrResult
import QrStyle
import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.provider.ContactsContract
import android.util.Log
import android.util.LruCache
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dong.baselib.file.saveImageToInternalStorage
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.database.AppDatabase
import net.blwsmartware.qrcodescanner.database.AppDbRepository
import net.blwsmartware.qrcodescanner.database.model.HistoryApp
import net.blwsmartware.qrcodescanner.model.ContactModel
import net.blwsmartware.qrcodescanner.model.CreateType
import net.blwsmartware.qrcodescanner.model.QrType
import net.blwsmartware.qrcodescanner.model.ScanModel
import net.blwsmartware.qrcodescanner.model.ScanType
import net.blwsmartware.qrcodescanner.qrgenerator.QrCodeGenerator
import net.blwsmartware.qrcodescanner.qrgenerator.QrData
import net.blwsmartware.qrcodescanner.qrgenerator.createQrOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

@Suppress("DEPRECATION")
class AppViewModel(private val app: Application) : AndroidViewModel(app) {
    var currentIndexNav = MutableLiveData<Int>(R.id.homeFragment)
    val dataImpl get() = AppDbRepository(AppDatabase.init(app.applicationContext).history())
    var scanResult = MutableStateFlow<ScanModel?>(null)
    var bitmapQrCode = MutableStateFlow<Bitmap?>(null)
    val createHistory = MutableStateFlow<HistoryApp?>(null)


    fun deleteHistory(history: HistoryApp){
        viewModelScope.launch {
            dataImpl.deleteHistory(history)
        }
    }



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

    fun queryAllContacts(contentResolver: ContentResolver): Flow<List<ContactModel>> {
        return flow {
            val contacts = mutableListOf<ContactModel>()
            val cursor: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null,
                null,
                null
            )

            cursor?.use {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (it.moveToNext()) {
                    val name = it.getString(nameIndex)
                    val phone = it.getString(numberIndex)
                    contacts.add(ContactModel(name, phone))
                }
            }
            emit(contacts)
        }.flowOn(Dispatchers.IO)
    }

    fun createQrWithValue(
        contentCreated: String,
        qrType: QrType = QrType.TEXT,
        callback: (Bitmap) -> Unit
    ) {
        viewModelScope.launch {

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
    fun createBarCodeWithValue(
        contentCreated: String,
        barcodeType: BarcodeType=BarcodeType.EAN_8,
        callback: (Bitmap) -> Unit
    ) {
        viewModelScope.launch {

            val result = QrGenerator.generateBarcode(contentCreated, barcodeType)
            when (result) {
                is QrResult.Success -> {
                    callback(result.bitmap)
                    saveQrToDb(contentCreated, result.bitmap, QrType.BARCODE, "")
                }

                is QrResult.Error -> {
                    Toast.makeText(app, result.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}

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












