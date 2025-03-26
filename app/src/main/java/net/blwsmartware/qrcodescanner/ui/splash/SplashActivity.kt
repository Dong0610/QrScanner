package net.blwsmartware.qrcodescanner.ui.splash

import com.dong.baselib.widget.delay
import io.sad.monster.dialog.AppPurchase
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.databinding.ActivitySplashBinding
import net.blwsmartware.qrcodescanner.ui.language.LanguageStartActivity
import kotlinx.coroutines.delay
import net.blwsmartware.qrcodescanner.app.finishFirstFlow
import net.blwsmartware.qrcodescanner.ui.main.MainActivity

class SplashActivity:BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    override fun backPressed() {
        
    }

    override fun initialize() {
        AppPurchase.getInstance(this@SplashActivity).initBilling(application)

        AppPurchase.getInstance(this).restorePurchases {
            delay(3000){
                if(!finishFirstFlow){
                    launchActivity<LanguageStartActivity>()
                }
                else{
                    launchActivity<MainActivity>()
                }

                finish()
            }
        }

    }

    override fun ActivitySplashBinding.onClick() {
        
    }

    override fun ActivitySplashBinding.setData() {
        
    }
}