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
import kotlinx.coroutines.withContext
import net.blwsmartware.qrcodescanner.R
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
                Log.d("VuLT", "initPurchase: skuListSubsFromStore = $mListDetails")

                if (mListDetails.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        binding.tvCost1.text = AppPurchase.getPriceSub(mListDetails[0])
                        binding.tvCost2.text = AppPurchase.getPriceSub(mListDetails[1])
                        binding.tvCost3.text = AppPurchase.getPriceSub(mListDetails[2])
                        binding.tvCost4.text = AppPurchase.getPriceSub(mListDetails[3])
                        binding.tvCost5.text = AppPurchase.getPriceSub(mListDetails[4])
                        binding.tvCost6.text = AppPurchase.getPriceSub(mListDetails[5])
                    }
                    mCurPosition = mListDetails.size - 1
                    for (i in mListDetails.indices) {
                        if (mListDetails[i].productId == AppPurchase.IAP_BARCODE) {
                            mCurPosition = i
                            break
                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun backPressed() {
        finish()
    }

    private fun updateSelectedItem(view: UiLinearLayout, position: Int) {
        val isSelected = mCurPosition == position

        val strokeColors = if (isSelected) {
            intArrayOf(fromColor("#5B6CF9"), fromColor("5B6CF9"), fromColor("5B6CF9"))
        } else {
            intArrayOf(fromColor("#141A83FA"), fromColor("141A83FA"), fromColor("141A83FA"))
        }

        val bgColor = if (isSelected) {
            fromColor("#ffffff")
        } else {
            fromColor("#ffffff")
        }

        view.apply {
            setGradientStroke(strokeColors)
            setGradientStrokeOrientation(GradientOrientation.TL_BR)
            setBgColor(bgColor)
        }
    }

    private fun updateAllSelectedItems() {
        binding.apply {
            updateSelectedItem(llPackage1, 0)
            updateSelectedItem(llPackage2, 1)
            updateSelectedItem(llPackage3, 2)
            updateSelectedItem(llPackage4, 3)
            updateSelectedItem(llPackage5, 4)
            updateSelectedItem(llPackage6, 5)
        }
    }

    override fun initialize() {
        updateAllSelectedItems()
    }

    override fun ActivityPremiumBinding.onClick() {
        setPurchaseListener()
        binding.frClose.click {
            finish()
        }

        binding.apply {
            llPackage1.click {
                mCurPosition = 0
                updateAllSelectedItems()
            }
            llPackage2.click {
                mCurPosition = 1
                updateAllSelectedItems()
            }
            llPackage3.click {
                mCurPosition = 2
                updateAllSelectedItems()
            }
            llPackage4.click {
                mCurPosition = 3
                updateAllSelectedItems()
            }
            llPackage5.click {
                mCurPosition = 4
                updateAllSelectedItems()
            }
            llPackage6.click {
                mCurPosition = 5
                updateAllSelectedItems()
            }
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