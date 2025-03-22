package net.blwsmartware.qrcodescanner.ui.result

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CalendarContract
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.dong.baselib.file.saveImageToDownloads
import com.dong.baselib.file.shareFileWithPath
import com.dong.baselib.widget.click
import com.dong.baselib.widget.loadImage
import com.dong.baselib.widget.visible
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.app.countGrantLocation
import net.blwsmartware.qrcodescanner.app.toastShort
import net.blwsmartware.qrcodescanner.app.viewModel
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.builder.UrlUtils
import net.blwsmartware.qrcodescanner.builder.detectQrType
import net.blwsmartware.qrcodescanner.builder.resultBarcode
import net.blwsmartware.qrcodescanner.builder.resultContact
import net.blwsmartware.qrcodescanner.builder.resultEmail
import net.blwsmartware.qrcodescanner.builder.resultEvent
import net.blwsmartware.qrcodescanner.builder.resultLocation
import net.blwsmartware.qrcodescanner.builder.resultMessage
import net.blwsmartware.qrcodescanner.builder.resultPhone
import net.blwsmartware.qrcodescanner.builder.resultText
import net.blwsmartware.qrcodescanner.builder.resultUrl
import net.blwsmartware.qrcodescanner.builder.resultWifi
import net.blwsmartware.qrcodescanner.databinding.ActivityResultCreateBinding
import net.blwsmartware.qrcodescanner.model.EventModel
import net.blwsmartware.qrcodescanner.model.LocationModel
import net.blwsmartware.qrcodescanner.model.MapUtils
import net.blwsmartware.qrcodescanner.model.QrType
import net.blwsmartware.qrcodescanner.model.connectToWifi
import net.blwsmartware.qrcodescanner.model.fromVCard
import net.blwsmartware.qrcodescanner.model.parseSmsString
import net.blwsmartware.qrcodescanner.model.parseWifiString
import net.blwsmartware.qrcodescanner.model.stringToMailModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
class ResultCreateActivity :
    BaseActivity<ActivityResultCreateBinding>(ActivityResultCreateBinding::inflate) {
    override fun backPressed() {
        finish()
    }

    override fun initialize() {

    }

    private var dataResult = ""
    private var currentPath = ""
    override fun ActivityResultCreateBinding.onClick() {
        icCoppy.click {
            copyToClipBoard(dataResult) {
                toastShort(getString(R.string.coppy_success))
            }
        }
        icBack.click {
            backPressed()
        }
        icShare.click {
            shareFileWithPath(this@ResultCreateActivity, currentPath)
        }
        icSave.click {
            saveImageToDownloads(
                currentPath,
                "QrSan-QrGenerator${System.currentTimeMillis()}.png",
                "QrSan-QrGenerator"
            ) {
                if (it != "") {
                    toastShort(getString(R.string.save_to_gallery))
                } else {
                    toastShort(getString(R.string.save_to_gallery_fail))
                }
            }

        }
        icOther.click {
            when (qrType) {
                QrType.CONTACT -> {
                    val contact = fromVCard(dataResult)
                    val intents = Intent(Intent.ACTION_INSERT).apply {
                        type = ContactsContract.RawContacts.CONTENT_TYPE
                        putExtra(ContactsContract.Intents.Insert.NAME, contact.name)
                        putExtra(ContactsContract.Intents.Insert.PHONE, contact.phone)
                        putExtra(ContactsContract.Intents.Insert.EMAIL, contact.email)
                        putExtra(ContactsContract.Intents.Insert.POSTAL, contact.address)
                        putExtra(ContactsContract.Intents.Insert.COMPANY, contact.company)
                        putExtra(ContactsContract.Intents.Insert.NOTES, contact.note)

                    }
                    startActivity(intents)
                }

                QrType.EMAIL -> {
                    val emailModel = stringToMailModel(dataResult)
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(emailModel.email))
                        putExtra(Intent.EXTRA_SUBJECT, emailModel.subject)
                        putExtra(Intent.EXTRA_TEXT, emailModel.body)
                    }

                    try {
                        startActivity(emailIntent)
                    } catch (e: Exception) {
                        toastShort(
                            getString(R.string.no_email_app)
                        )
                    }
                }

                QrType.PHONE->{
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${dataResult}")
                    }
                    startActivity(dialIntent)
                }
                QrType.URL -> {
                    UrlUtils.openUrl(this@ResultCreateActivity, dataResult)
                }

                QrType.EVENT -> {
                    val eventModel = EventModel.fromQRCodeString(dataResult)
                    val startMillis: Long = convertDateFormat(
                        eventModel?.startTime ?: System.currentTimeMillis().toString()
                    ).let {
                        SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss",
                            Locale.getDefault()
                        ).parse(it)?.time
                            ?: 0L
                    }

                    val endMillis: Long = convertDateFormat(
                        eventModel?.endTime ?: System.currentTimeMillis().toString()
                    ).let {
                        SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss",
                            Locale.getDefault()
                        ).parse(it)?.time
                            ?: 0L
                    }


                    val calendarIntent = Intent(Intent.ACTION_INSERT).apply {
                        data = CalendarContract.Events.CONTENT_URI
                        putExtra(CalendarContract.Events.TITLE, eventModel?.eventName)
                        putExtra(CalendarContract.Events.EVENT_LOCATION, eventModel?.location)
                        putExtra(CalendarContract.Events.DESCRIPTION, eventModel?.description)
                        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                    }

                    startActivity(calendarIntent)
                }

                QrType.LOCATION -> {
                    LocationModel.fromString(dataResult)?.let { it ->
                        MapUtils.openMap(this@ResultCreateActivity, it)
                    }
                }


                QrType.SMS -> {
                    parseSmsString(dataResult)?.let {
                        val smsUri = "smsto:${it.phone}".toUri()
                        val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
                            putExtra("sms_body", it.message)
                        }
                        startActivity(smsIntent)
                    }
                }

                QrType.WIFI -> {
                    checkAndRequestPermission()
                }

                else -> {}
            }
        }

    }

    private fun convertDateFormat(originalDateString: String): String {
        val originalFormat = SimpleDateFormat("ddMMyyyyHHmm", Locale.getDefault())
        val targetFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        return try {
            val date = originalFormat.parse(originalDateString)

            targetFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            connectWifi()
        } else {
            handlePermissionDenial()
        }
    }

    private fun connectWifi() {
        val wifi = parseWifiString(dataResult)
        connectToWifi(applicationContext, wifi?.pass ?: "", wifi?.name ?: "")
    }

    private fun handlePermissionDenial() {
        if (!shouldShowRequestPermissionRationale(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            countGrantLocation++
            if (countGrantLocation > 1) {
                gotToSetting(TypeGoSettings.LOCATION) {}
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun checkAndRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                connectWifi()
            }

            shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                gotToSetting(TypeGoSettings.LOCATION) {}
            }

            else -> {
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }


    var qrType: QrType = QrType.TEXT

    override fun ActivityResultCreateBinding.setData() {
        lifecycleScope.launch {
            viewModel.createHistory.collectLatest { data ->
                data?.let {
                    imgResult.loadImage(data.scanImage)
                    qrType = detectQrType(data.scanVales)
                    dataResult = data.scanVales
                    currentPath = data.scanImage
                    qrType.let {
                        when (qrType) {
                            QrType.CONTACT -> {
                                resultContact(lnResult, fromVCard(data.scanVales))
                                icOther.visible()
                                icOther.setImageResource(R.drawable.ic_result_contact)
                            }

                            QrType.EMAIL -> {
                                resultEmail(lnResult, stringToMailModel(data.scanVales))
                                icOther.visible()
                                icOther.setImageResource(R.drawable.ic_result_mail)
                            }

                            QrType.PHONE -> {
                                resultPhone(lnResult, data.scanVales)
                                icOther.visible()
                                icOther.setImageResource(R.drawable.ic_result_call)
                            }
                            QrType.URL -> {
                                resultUrl(lnResult, data.scanVales)
                                icOther.visible()
                                icOther.setImageResource(R.drawable.ic_result_search)
                            }

                            QrType.EVENT -> {
                                EventModel.fromQRCodeString(data.scanVales)
                                    ?.let { it1 -> resultEvent(lnResult, it1) }
                                icOther.visible()
                                icOther.setImageResource(R.drawable.ic_result_event)
                            }

                            QrType.LOCATION -> {
                                LocationModel.fromString(data.scanVales)
                                    ?.let { it1 -> resultLocation(lnResult, it1) }
                                icOther.visible()
                                icOther.setImageResource(R.drawable.ic_result_map)
                            }

                            QrType.SMS -> {
                                resultMessage(lnResult, parseSmsString(data.scanVales))
                                icOther.visible()
                                icOther.setImageResource(R.drawable.ic_result_sms)
                            }

                            QrType.WIFI -> {
                                parseWifiString(data.scanVales)?.let { it1 ->
                                    resultWifi(
                                        lnResult,
                                        it1
                                    )
                                }
                                icOther.visible()
                                icOther.setImageResource(R.drawable.ic_result_wifi)
                            }

                            QrType.BARCODE -> {
                                resultBarcode(lnResult, data.scanVales)
                                lnMenu.removeView(icOther)
                            }

                            else -> {
                                resultText(lnResult, data.scanVales)
                                lnMenu.removeView(icOther)
                            }
                        }
                    }
                }
            }
        }
    }

}