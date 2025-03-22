package net.blwsmartware.qrcodescanner.ui.main

import android.annotation.SuppressLint
import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.BuildConfig
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.databinding.ActivityAboutBinding

class AboutActivity : BaseActivity<ActivityAboutBinding>(ActivityAboutBinding::inflate) {
     override fun backPressed() {
        finish()
    }

    override fun initialize() {

    }

    override fun ActivityAboutBinding.onClick() {

        ivBackPress.click {
            backPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun ActivityAboutBinding.setData() {
        txtVersion.text = getString(R.string.version)  + BuildConfig.VERSION_NAME
    }

}