package com.project.qrscanner.ui.language

import android.widget.Toast
import com.dong.baselib.widget.click
import com.project.qrscanner.R
import com.project.qrscanner.adapter.LanguageAdapter
import com.project.qrscanner.base.BaseActivity
import com.project.qrscanner.base.SystemUtil
import com.project.qrscanner.databinding.ActivityLanguageStartBinding
import com.project.qrscanner.ui.permission.PermissionActivity
import java.util.Locale


class LanguageStartActivity :
    BaseActivity<ActivityLanguageStartBinding>(ActivityLanguageStartBinding::inflate) {

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
        launchActivity<PermissionActivity>()
        finish()
    }
}