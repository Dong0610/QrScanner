package com.project.qrscanner.ui.create

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import com.dong.baselib.lifecycle.mutableLiveData
import com.dong.baselib.widget.afterTextChanged
import com.dong.baselib.widget.click
import com.project.qrscanner.R
import com.project.qrscanner.app.viewModel
import com.project.qrscanner.base.BaseActivity
import com.project.qrscanner.builder.setState
import com.project.qrscanner.builder.textViewError
import com.project.qrscanner.databinding.ActivityCreateTextBinding
import com.project.qrscanner.databinding.ActivityCreateUrlBinding
import com.project.qrscanner.model.QrType
import com.project.qrscanner.ui.result.ResultCreateActivity

class CreateUrlActivity :
    BaseActivity<ActivityCreateUrlBinding>(ActivityCreateUrlBinding::inflate) {
    override fun backPressed() {
        finish()
    }

    override fun initialize() {
        errorText.textViewError(binding.txtError, this@CreateUrlActivity)
    }

    private var errorText = mutableLiveData("")

    private fun getTextFromClipboard(context: Context): String {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return if (clipboard.hasPrimaryClip()) {
            clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
        } else ""
    }

    override fun ActivityCreateUrlBinding.onClick() {
        btnBack.click {
            backPressed()
        }

        btnCreate.click {
            val value = etBody.text.toString()
            hideKeyboard()
            if (value.isBlank()) {
                errorText.value = getString(R.string.require_value_enter)
            } else {
                if (value.length > 199) {
                    errorText.value =
                        getString(R.string.maximum) + " 800 " + getString(R.string.character)
                } else {
                    viewModel.createQrWithValue(value,QrType.URL) {
                        launchActivity<ResultCreateActivity>()
                    }
                }
            }
        }


    }

    @SuppressLint("SetTextI18n")
    override fun ActivityCreateUrlBinding.setData() {
        etBody.afterTextChanged {
            tvCharCount.text = "${it.length}/800"
            if (it.length > 199) {
                errorText.value =
                    getString(R.string.maximum) + " 800 " + getString(R.string.character)
                btnCreate.setState(false)
            } else {
                if (it.trim().isNotEmpty()) {
                    btnCreate.setState(true)
                    errorText.value = null
                }

            }
        }
    }

}