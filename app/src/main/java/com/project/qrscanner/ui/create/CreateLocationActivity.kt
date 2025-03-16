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
import com.project.qrscanner.databinding.ActivityCreateLocationBinding
import com.project.qrscanner.model.EmailModel
import com.project.qrscanner.model.LocationModel
import com.project.qrscanner.model.QrType
import com.project.qrscanner.model.toMailtoString
import com.project.qrscanner.ui.result.ResultCreateActivity

class CreateLocationActivity :
    BaseActivity<ActivityCreateLocationBinding>(ActivityCreateLocationBinding::inflate) {
    override fun backPressed() {
        finish()
    }


    private val messageError = MutableLiveData<String>(" ")
    private val longError = mutableLiveData("_")
    private val latError = mutableLiveData(" ")

    override fun initialize() {
        latError.textViewError(binding.txtErrorLatitude, this)
        longError.textViewError(binding.txtErrorLongitude, this@CreateLocationActivity)
        messageError.textViewError(binding.txtError, this)
    }


    var isNextSc = false
    override fun onResume() {
        super.onResume()
        isNextSc = false
    }

    override fun ActivityCreateLocationBinding.onClick() {
        btnBack.click {
            backPressed()
        }
        btnCreate.click {
            hideKeyboard()
            if (btnCreate.isActivated && !isNextSc) {
                isNextSc = true
                val value = LocationModel(
                    binding.etLongitude.text.toString(),
                    binding.etLocation.text.toString(),
                    binding.etNote.text.toString()
                ).toString()
                viewModel.createQrWithValue(value, QrType.LOCATION) {
                    launchActivity<ResultCreateActivity>()
                }

            }
        }

    }

    override fun ActivityCreateLocationBinding.setData() {
        etLocation.afterTextChanged {
            longError.set(
                when {
                    it.toDoubleOrNull() == null -> getString(R.string.invalid_latitude_format)
                    (it.toDouble() !in -90.0..90.0) -> getString(R.string.latitude_out_of_range)
                    else -> "_"
                }
            )
        }
        etLongitude.afterTextChanged {
            longError.set(
                when {
                    it.toDoubleOrNull() == null -> getString(R.string.invalid_longitude_format)
                    it.toDouble() !in -180.0..180.0 -> getString(R.string.longitude_out_of_range)
                    else -> "_"
                }
            )
        }
        etNote.afterTextChanged {
            if (it.trim().isEmpty()) {
                messageError.set(getString(R.string.require_value_enter))
            }else
                if (it.length > 500) {
                    messageError.set(getString(R.string.maximum) + " 500 " + getString(R.string.character))
                } else {
                    messageError.set("_")
                }
        }

        LauncherEffect(messageError, latError,longError) {
            val longError = longError.get() == "_"
            val isSmsErrorEmpty = messageError.get() == "_"
            val latError = latError.get() == "_"
            btnCreate.setState(latError && isSmsErrorEmpty && longError)
        }
    }
}