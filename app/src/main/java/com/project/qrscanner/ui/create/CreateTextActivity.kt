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
import com.project.qrscanner.ui.result.ResultCreateActivity

class CreateTextActivity :
    BaseActivity<ActivityCreateTextBinding>(ActivityCreateTextBinding::inflate) {
    override fun backPressed() {
        finish()
    }

    override fun initialize() {
        errorText.textViewError(binding.txtError, this@CreateTextActivity)
    }

    private var errorText = mutableLiveData("")

    private fun getTextFromClipboard(context: Context): String {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return if (clipboard.hasPrimaryClip()) {
            clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
        } else ""
    }

    override fun ActivityCreateTextBinding.onClick() {
        btnBack.click {
            backPressed()
        }
        icPaste.click {
            val text = getTextFromClipboard(this@CreateTextActivity)
            if (text.isNotBlank()) {
                if (text.length > 201) {
                    etBody.setText(text.substring(0, 199))
                } else {
                    etBody.setText(text)
                }
                errorText.value = ""
            } else {
                errorText.value =
                    getString(R.string.maximum) + " 200 " + getString(R.string.character)
            }
        }
        btnCreate.click {
            hideKeyboard()
            val value = etBody.text.toString()
            if (value.isBlank()) {
                errorText.value = getString(R.string.require_value_enter)
            } else {
                if (value.length > 199) {
                    errorText.value =
                        getString(R.string.maximum) + " 200 " + getString(R.string.character)
                } else {
                    viewModel.createQrWithValue(value) {
                        launchActivity<ResultCreateActivity>()
                    }
                }
            }
        }


    }

    @SuppressLint("SetTextI18n")
    override fun ActivityCreateTextBinding.setData() {
        etBody.afterTextChanged {
            tvCharCount.text = "${it.length}/200"
            if (it.length > 199) {
                errorText.value =
                    getString(R.string.maximum) + " 200 " + getString(R.string.character)
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