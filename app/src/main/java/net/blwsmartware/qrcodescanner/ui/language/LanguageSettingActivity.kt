package net.blwsmartware.qrcodescanner.ui.language;

import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.adapter.LanguageAdapter
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.base.SystemUtil
import net.blwsmartware.qrcodescanner.databinding.ActivityLanguageSettingBinding
import net.blwsmartware.qrcodescanner.ui.main.MainActivity
import java.util.Locale

class LanguageSettingActivity :
    BaseActivity<ActivityLanguageSettingBinding>(ActivityLanguageSettingBinding::inflate) {

    private var codeLang: String? = null
    private fun onNextActivity() {
        launchActivity<MainActivity>()
        finishAffinity()

    }


    override fun backPressed() {
        finish()
        setResult(RESULT_OK)
    }

    override fun initialize() {
        
    }

    override fun ActivityLanguageSettingBinding.setData() {
        codeLang = Locale.getDefault().language

        val listLanguage = getLanguageArray().map {
            if (it.code == codeLang) {
                it.active = true
            }
            it
        }.toMutableList()
        val languageAdapter = LanguageAdapter(onClick = {
            codeLang = it
        })
        listLanguage.let { languageAdapter.submitList(it) }
        binding.rcvLanguage.adapter = languageAdapter
    }

    override fun ActivityLanguageSettingBinding.onClick() {
        binding.ivBackPress.click { backPressed() }
        binding.ivSaved.click {
            SystemUtil.saveLocale(this@LanguageSettingActivity, codeLang)
            onNextActivity()
        }
    }
}
