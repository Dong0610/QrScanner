package com.project.qrscanner.ui.splash

import com.dong.baselib.widget.delay
import com.project.qrscanner.base.BaseActivity
import com.project.qrscanner.databinding.ActivitySplashBinding
import com.project.qrscanner.ui.language.LanguageStartActivity
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