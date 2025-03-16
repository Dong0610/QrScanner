package com.project.qrscanner.ui.create

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dong.baselib.lifecycle.LauncherEffect
import com.dong.baselib.lifecycle.get
import com.dong.baselib.lifecycle.mutableLiveData
import com.dong.baselib.lifecycle.set
import com.dong.baselib.widget.afterTextChanged
import com.dong.baselib.widget.click
import com.project.qrscanner.R
import com.project.qrscanner.app.viewModel
import com.project.qrscanner.base.BaseActivity
import com.project.qrscanner.builder.setState
import com.project.qrscanner.builder.textViewError
import com.project.qrscanner.databinding.ActivityCreateEmailBinding
import com.project.qrscanner.model.EmailModel
import com.project.qrscanner.model.MessageModel
import com.project.qrscanner.model.QrType
import com.project.qrscanner.model.toMailtoString
import com.project.qrscanner.model.toSmsString
import com.project.qrscanner.ui.result.ResultCreateActivity

class CreateEmailActivity :
    BaseActivity<ActivityCreateEmailBinding>(ActivityCreateEmailBinding::inflate) {
    override fun backPressed() {
        finish()
    }


    private val messageError = MutableLiveData<String>(" ")
    private val subjectError = mutableLiveData("_")
    private val emailError = mutableLiveData(" ")

    override fun initialize() {
        emailError.textViewError(binding.txtErrorEmail,this)
        subjectError.textViewError(binding.txtErrorSubject, this@CreateEmailActivity)
        messageError.textViewError(binding.txtError, this)
    }


    var isNextSc=false
    override fun onResume() {
        super.onResume()
        isNextSc=false
    }

    override fun ActivityCreateEmailBinding.onClick() {
        btnBack.click {
            backPressed()
        }
        btnCreate.click {
            hideKeyboard()
            if (btnCreate.isActivated && !isNextSc) {
                isNextSc= true
                val value= EmailModel(binding.etEmail.text.toString(),binding.etSubject.text.toString(),binding.etBody.text.toString()).toMailtoString()
                viewModel.createQrWithValue(value,QrType.EMAIL){
                    launchActivity<ResultCreateActivity>()
                }

            }
        }
    }

    override fun ActivityCreateEmailBinding.setData() {
        etBody.afterTextChanged {
            if (it.trim().isEmpty()) {
                messageError.set(getString(R.string.require_value_enter))
            }else
            if (it.length > 500) {
                messageError.set(getString(R.string.maximum) + " 500 " + getString(R.string.character))
            } else {
                messageError.set("_")
            }
            tvCharCount.text = "${it.length}/500"
        }
        etSubject.afterTextChanged {
            if (it.trim().isEmpty()) {
                subjectError.set("_")
            } else if(it.length>300){
                subjectError.set(getString(R.string.maximum) + " 300 " + getString(R.string.character))
            }
            else {
                subjectError.set("_")
            }
        }
        etEmail.afterTextChanged {
            if(it.isEmpty()){
                emailError.set(getString(R.string.require_value_enter))
            }
            else if(!isValidEmail(it)){
                emailError.set(getString(R.string.invalid_email_format))
            }
            else {
                emailError.set("_")
            }
        }

        LauncherEffect(messageError,emailError, subjectError) {
            val isPhoneErrorEmpty = subjectError.get()=="_"
            val isSmsErrorEmpty = messageError.get()=="_"
            val isEmailErrorEmpty = emailError.get()=="_"

            Log.e("TestValue", "Data is: $isSmsErrorEmpty $isPhoneErrorEmpty data: ")
            btnCreate.setState(isPhoneErrorEmpty && isSmsErrorEmpty && isEmailErrorEmpty)
        }
    }
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        return email.matches(emailRegex.toRegex())
    }
}