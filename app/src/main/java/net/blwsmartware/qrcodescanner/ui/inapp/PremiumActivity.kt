package net.blwsmartware.qrcodescanner.ui.inapp

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.ProductDetails
import com.dong.baselib.widget.click
import io.sad.monster.callback.PurchaseListener
import io.sad.monster.dialog.AppPurchase
import io.sad.monster.util.SharePreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.adapter.PurchasePackageAdapter
import net.blwsmartware.qrcodescanner.app.isCreateBarcode
import net.blwsmartware.qrcodescanner.app.isEmailCreateQr
import net.blwsmartware.qrcodescanner.app.isLocationCreateQr
import net.blwsmartware.qrcodescanner.app.isMessageCreateQr
import net.blwsmartware.qrcodescanner.app.isPhoneCreateQr
import net.blwsmartware.qrcodescanner.app.isUrlCreateQr
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.databinding.ActivityPremiumBinding
import net.blwsmartware.qrcodescanner.ui.main.MainActivity

class PremiumActivity : BaseActivity<ActivityPremiumBinding>(ActivityPremiumBinding::inflate) {

    private var mListDetails = arrayListOf<ProductDetails>()

    private fun setPurchaseListener() {
        AppPurchase.getInstance(this).setPurchaseListener(object : PurchaseListener {
            override fun onProductPurchased(productId: String, transactionDetails: String) {
                SharePreferenceUtils.putIsPurchase(this@PremiumActivity, true)
                AppPurchase.getInstance(this@PremiumActivity).setIsPurchased(true)
                setValuePurchase(productId)
                val intent = Intent(
                    this@PremiumActivity,
                    MainActivity::class.java
                )
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                AppPurchase.getInstance(this@PremiumActivity).restorePurchases {
                    startActivity(intent)
                    finish()
                }
            }

            override fun displayErrorMessage(errorMsg: String) {}
            override fun onUserCancelBilling() {}
            override fun onUserPurchaseConsumable() {
            }
        })
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

    private fun initPurchase() {
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                mListDetails.clear()
                mListDetails.addAll(AppPurchase.getInstance(this@PremiumActivity).skuListINAPFromStore)
                Log.e("VuLT", "initPurchase: skuListSubsFromStore = $mListDetails")

                if (mListDetails.isNotEmpty()) {
                    setUpAdapter()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var adapter: PurchasePackageAdapter? = null
    private fun setUpAdapter() {
        // Khởi tạo và gán Adapter
        adapter = PurchasePackageAdapter(mListDetails) { selectedPackage ->
            // Xử lý khi người dùng chọn mua gói
            handlePurchase(selectedPackage)
        }

        binding.rcvData.adapter = adapter
    }

    private fun handlePurchase(package1: ProductDetails) {
        // Xử lý logic mua hàng ở đây
        Log.d("VuLT", "handlePurchase: $package1")
        AppPurchase.getInstance(this@PremiumActivity)
            .purchase(this@PremiumActivity, package1)
    }

    override fun backPressed() {
        finish()
    }

    override fun initialize() {
        setPurchaseListener()
    }

    override fun ActivityPremiumBinding.onClick() {
        binding.frClose.click {
            finish()
        }
    }

    override fun ActivityPremiumBinding.setData() {
        initPurchase()

    }

}