package net.blwsmartware.qrcodescanner.ui.inapp

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.ProductDetails
import com.dong.baselib.builder.fromColor
import com.dong.baselib.widget.GradientOrientation
import com.dong.baselib.widget.click
import com.dong.baselib.widget.layout.UiLinearLayout
import io.sad.monster.callback.PurchaseListener
import io.sad.monster.dialog.AppPurchase
import io.sad.monster.util.SharePreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.adapter.PurchasePackageAdapter
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.databinding.ActivityPremiumBinding
import net.blwsmartware.qrcodescanner.ui.main.MainActivity

class PremiumActivity : BaseActivity<ActivityPremiumBinding>(ActivityPremiumBinding::inflate) {

    private var mCurPosition = 0
    private var mListDetails = arrayListOf<ProductDetails>()

    private fun setPurchaseListener() {
        AppPurchase.getInstance(this).setPurchaseListener(object : PurchaseListener {
            override fun onProductPurchased(productId: String?, transactionDetails: String) {
                SharePreferenceUtils.putIsPurchase(this@PremiumActivity, true)
                AppPurchase.getInstance(this@PremiumActivity).setIsPurchased(true)
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

    private fun setUpAdapter() {
        // Khởi tạo và gán Adapter
        val adapter = PurchasePackageAdapter(mListDetails) { selectedPackage ->
            // Xử lý khi người dùng chọn mua gói
            handlePurchase(selectedPackage)
        }

        binding.rcvData.adapter = adapter
    }

    private fun handlePurchase(package1: ProductDetails) {
        // Xử lý logic mua hàng ở đây
        Toast.makeText(this, "Đang xử lý mua ${package1.name}", Toast.LENGTH_SHORT).show()
        AppPurchase.getInstance(this@PremiumActivity)
            .purchase(this@PremiumActivity, package1)
    }

    override fun backPressed() {
        finish()
    }

    override fun initialize() {
    }

    override fun ActivityPremiumBinding.onClick() {
        setPurchaseListener()
        binding.frClose.click {
            finish()
        }

        binding.tvSubscribe.click {
            if (mListDetails.isNotEmpty()) {
                if (mCurPosition >= 0 && mCurPosition < mListDetails.size) {
                    AppPurchase.getInstance(this@PremiumActivity).IAP_BUY_REPEAT = false
                    val details = mListDetails[mCurPosition]
                    AppPurchase.getInstance(this@PremiumActivity)
                        .purchase(this@PremiumActivity, details)
                } else {
                    Toast.makeText(
                        this@PremiumActivity,
                        getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@click
            }
            Toast.makeText(
                this@PremiumActivity,
                getString(R.string.error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun ActivityPremiumBinding.setData() {
        initPurchase()

    }

}