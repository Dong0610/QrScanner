package net.blwsmartware.qrcodescanner.ui.create

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dong.baselib.lifecycle.LauncherEffect
import com.dong.baselib.lifecycle.get
import com.dong.baselib.lifecycle.mutableLiveData
import com.dong.baselib.lifecycle.set
import com.dong.baselib.widget.afterTextChanged
import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.app.viewModel
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.builder.setState
import net.blwsmartware.qrcodescanner.builder.textViewError
import net.blwsmartware.qrcodescanner.databinding.ActivityCreateSmsBinding
import net.blwsmartware.qrcodescanner.model.MessageModel
import net.blwsmartware.qrcodescanner.model.QrType
import net.blwsmartware.qrcodescanner.model.toSmsString
import net.blwsmartware.qrcodescanner.ui.result.ResultCreateActivity

class CreateMessageActivity :
    BaseActivity<ActivityCreateSmsBinding>(ActivityCreateSmsBinding::inflate) {
    override fun backPressed() {
        finish()
    }

    override fun initialize() {
        phoneError.textViewError(binding.txtErrorPhone, this@CreateMessageActivity)
        smsError.textViewError(binding.txtError, this)
    }


    private val smsError = MutableLiveData<String>(" ")
    private val phoneError = mutableLiveData(" ")
    var isNextSc=false
    override fun onResume() {
        super.onResume()
        isNextSc=false
    }

    override fun ActivityCreateSmsBinding.onClick() {
        btnBack.click {
            backPressed()
        }
        btnCreate.click {
            hideKeyboard()
            if (btnCreate.isActivated && !isNextSc) {
                isNextSc= true
                val value =
                    MessageModel(edtPhone.text.toString(), etBody.text.toString()).toSmsString()
                viewModel.createQrWithValue(value, QrType.SMS) {
                    launchActivity<ResultCreateActivity>()
                }
            }
        }
    }

    override fun ActivityCreateSmsBinding.setData() {
        etBody.afterTextChanged {
            if (it.trim().isEmpty()) {
                smsError.set(getString(R.string.require_value_enter))
            }else
            if (it.length > 500) {
                smsError.set(getString(R.string.maximum) + " 500 " + getString(R.string.character))
            } else {
                smsError.set("_")
            }
        }
        edtPhone.afterTextChanged {
            if (it.trim().isEmpty()) {
                phoneError.set(getString(R.string.require_value_enter))
            } else {
                phoneError.set("_")
            }
        }

        LauncherEffect(smsError, phoneError) {
            val isPhoneErrorEmpty = phoneError.get()=="_"
            val isSmsErrorEmpty = smsError.get()=="_"

            Log.e("TestValue", "Data is: $isSmsErrorEmpty $isPhoneErrorEmpty data: ")
            btnCreate.setState(isPhoneErrorEmpty && isSmsErrorEmpty)
        }
    }
}