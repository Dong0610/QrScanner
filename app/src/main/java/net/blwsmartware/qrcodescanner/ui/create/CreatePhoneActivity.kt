package net.blwsmartware.qrcodescanner.ui.create

import com.dong.baselib.lifecycle.mutableLiveData
import com.dong.baselib.lifecycle.set
import com.dong.baselib.widget.afterTextChanged
import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.app.viewModel
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.builder.setState
import net.blwsmartware.qrcodescanner.builder.textViewError
import net.blwsmartware.qrcodescanner.databinding.ActivityCreatePhoneBinding
import net.blwsmartware.qrcodescanner.model.LocationModel
import net.blwsmartware.qrcodescanner.model.QrType
import net.blwsmartware.qrcodescanner.ui.result.ResultCreateActivity

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
        isNextSc=false
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
                viewModel.createQrWithValue("tel:$value", QrType.PHONE) {
                    launchActivity<ResultCreateActivity>()
                }

            }
        }
    }

    override fun ActivityCreatePhoneBinding.setData() {
        edtPhone.afterTextChanged {
            if(it.trim().isEmpty()){
                phoneError.set(getString(R.string.please_fill_outline))
                btnCreate.setState(false)
            }
            else if(it.length>=29){
                phoneError.set(getString(R.string.phone_number_should_not_exceed_29_characters))
                btnCreate.setState(false)
            }
            else{
                phoneError.set("_")
                btnCreate.setState(true)
            }
        }
    }
}