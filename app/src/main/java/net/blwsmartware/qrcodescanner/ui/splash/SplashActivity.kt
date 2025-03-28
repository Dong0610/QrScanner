package net.blwsmartware.qrcodescanner.ui.splash

import android.util.Log
import com.dong.baselib.widget.delay
import io.sad.monster.dialog.AppPurchase
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.databinding.ActivitySplashBinding
import net.blwsmartware.qrcodescanner.ui.language.LanguageStartActivity
import kotlinx.coroutines.delay
import net.blwsmartware.qrcodescanner.app.finishFirstFlow
import net.blwsmartware.qrcodescanner.app.isCreateBarcode
import net.blwsmartware.qrcodescanner.app.isEmailCreateQr
import net.blwsmartware.qrcodescanner.app.isLocationCreateQr
import net.blwsmartware.qrcodescanner.app.isMessageCreateQr
import net.blwsmartware.qrcodescanner.app.isPhoneCreateQr
import net.blwsmartware.qrcodescanner.app.isUrlCreateQr
import net.blwsmartware.qrcodescanner.ui.main.MainActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    override fun backPressed() {

    }

    override fun initialize() {
        AppPurchase.getInstance(this@SplashActivity).initBilling(application)

        AppPurchase.getInstance(this).restorePurchases {
            checkRestore()
            delay(3000) {
                if (!finishFirstFlow) {
                    launchActivity<LanguageStartActivity>()
                } else {
                    launchActivity<MainActivity>()
                }

                finish()
            }
        }

    }

    private fun checkRestore() {
        val lt = AppPurchase.getInstance(this).listIdReStore
        if (!lt.isNullOrEmpty()){
            for (item in lt) {
                setValuePurchase(item)
            }
        }
    }

    private fun setValuePurchase(productId: String) {
        when (productId) {
            "email_create_qr" -> {
                isEmailCreateQr = true
            }

            "localtion_create_qr" -> {
                isLocationCreateQr = true
            }

            "message_create_qr" -> {
                isMessageCreateQr = true
            }

            "phone_create_qr" -> {
                isPhoneCreateQr = true
            }

            "qr_create_barcode" -> {
                isCreateBarcode = true
            }

            "url_create_qr" -> {
                isUrlCreateQr = true
            }
        }
    }


    override fun ActivitySplashBinding.onClick() {

    }

    override fun ActivitySplashBinding.setData() {

    }
}