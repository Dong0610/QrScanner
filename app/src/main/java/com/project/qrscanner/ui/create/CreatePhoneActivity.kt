package com.project.qrscanner.ui.create

import com.dong.baselib.lifecycle.mutableLiveData
import com.dong.baselib.lifecycle.set
import com.dong.baselib.widget.click
import com.project.qrscanner.R
import com.project.qrscanner.app.viewModel
import com.project.qrscanner.base.BaseActivity
import com.project.qrscanner.builder.setState
import com.project.qrscanner.builder.textViewError
import com.project.qrscanner.databinding.ActivityCreatePhoneBinding
import com.project.qrscanner.model.LocationModel
import com.project.qrscanner.model.QrType
import com.project.qrscanner.ui.result.ResultCreateActivity

class CreatePhoneActivity: BaseActivity<ActivityCreatePhoneBinding>(ActivityCreatePhoneBinding::inflate) {
    override fun backPressed() {
        finish()
    }

    private var phoneError = mutableLiveData(" ")
    override fun initialize() {
        phoneError.textViewError(binding.txtError,this)
    }

    private var isNextSc = false
    override fun onResume() {
        super.onResume()
        isNextSc=true
    }

    override fun ActivityCreatePhoneBinding.onClick() {
        btnBack.click {
            backPressed()
        }
        btnCreate.click {
            hideKeyboard()
            if (btnCreate.isActivated && !isNextSc) {
                isNextSc = true
                val value =edtPhone.text.toString()
                viewModel.createQrWithValue(value, QrType.LOCATION) {
                    launchActivity<ResultCreateActivity>()
                }

            }
        }
    }

    override fun ActivityCreatePhoneBinding.setData() {
        phoneError.observe(this@CreatePhoneActivity){
            if(it.trim().isEmpty()){
                phoneError.set(getString(R.string.please_fill_outline))
                btnCreate.setState(false)
            }
            else if(it.length>=29){
                phoneError.set(getString(R.string.phone_number_should_not_exceed_29_characters))
                btnCreate.setState(false)
            }
            else{
                btnCreate.setState(true)
                phoneError.set("_")
            }
        }
    }
}