package net.blwsmartware.qrcodescanner.ui.splash

import com.dong.baselib.widget.delay
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.databinding.ActivitySplashBinding
import net.blwsmartware.qrcodescanner.ui.language.LanguageStartActivity
import kotlinx.coroutines.delay

class SplashActivity:BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    override fun backPressed() {
        
    }

    override fun initialize() {
        delay(3000){
            launchActivity<LanguageStartActivity>()
            finish()
        }
    }

    override fun ActivitySplashBinding.onClick() {
        
    }

    override fun ActivitySplashBinding.setData() {
        
    }
}