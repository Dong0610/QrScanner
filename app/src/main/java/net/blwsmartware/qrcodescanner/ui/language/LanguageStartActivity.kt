package net.blwsmartware.qrcodescanner.ui.language

import android.widget.Toast
import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.adapter.LanguageAdapter
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.base.SystemUtil
import net.blwsmartware.qrcodescanner.databinding.ActivityLanguageStartBinding
import net.blwsmartware.qrcodescanner.ui.intro.IntroActivity
import net.blwsmartware.qrcodescanner.ui.permission.PermissionActivity
import java.util.Locale


class LanguageStartActivity :
    BaseActivity<ActivityLanguageStartBinding>(ActivityLanguageStartBinding::inflate,true) {

    private var listLanguage: MutableList<LanguageModel> = mutableListOf()
    private var codeLang: String? = null

    override fun backPressed() {
        finishAffinity()
    }

    override fun initialize() {

    }


    override fun ActivityLanguageStartBinding.setData() {
        codeLang = Locale.getDefault().language
        listLanguage = getLanguageArray()
        val languageAdapter = LanguageAdapter(onClick = { codeLang = it })
        listLanguage.let { languageAdapter.submitList(it) }

        binding.rcvLanguage.adapter = languageAdapter

    }

    override fun ActivityLanguageStartBinding.onClick() {
        binding.icSaveLanguage.click {
            if (codeLang != null) {
                SystemUtil.saveLocale(this@LanguageStartActivity, codeLang)
                startNextActivity()
            } else {
                Toast.makeText(
                    this@LanguageStartActivity,
                    getString(R.string.please_choose_a_language),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startNextActivity() {
        launchActivity<IntroActivity>()
        finish()
    }
}