package net.blwsmartware.qrcodescanner.ui.main

import com.dong.baselib.listener.OnStateChangeListener
import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.app.isSound
import net.blwsmartware.qrcodescanner.app.isVibrate
import net.blwsmartware.qrcodescanner.app.shareApp
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.databinding.ActivitySettingBinding
import net.blwsmartware.qrcodescanner.ui.inapp.PremiumActivity
import net.blwsmartware.qrcodescanner.ui.language.LanguageSettingActivity

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {
    override fun backPressed() {
        finish()
    }

    override fun initialize() {
        binding.swSound.setState(isVibrate)
        binding.swVibrate.setState(isSound)
    }

    override fun ActivitySettingBinding.onClick() {
        ivBackPress.click {
            backPressed()
        }
    }

    override fun ActivitySettingBinding.setData() {
        ivPremium.click {
            launchActivity<PremiumActivity>()
        }
        llLanguage.click {
            launchActivity<LanguageSettingActivity>()
        }
        llAbout.click {
            launchActivity<AboutActivity>()
        }

        llShare.click {
            shareApp()
        }
        swVibrate.onStateChangeListener(object : OnStateChangeListener {
            override fun onStateChanged(state: Boolean) {
                isVibrate = state
            }
        })
        swSound.onStateChangeListener(object : OnStateChangeListener {
            override fun onStateChanged(state: Boolean) {
                isSound = state
            }
        })
    }
}