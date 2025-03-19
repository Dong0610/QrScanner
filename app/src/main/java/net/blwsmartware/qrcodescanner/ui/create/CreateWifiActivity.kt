package net.blwsmartware.qrcodescanner.ui.create

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
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
import net.blwsmartware.qrcodescanner.databinding.ActivityCreateWifiBinding
import net.blwsmartware.qrcodescanner.databinding.CustomPopupMenuBinding
import net.blwsmartware.qrcodescanner.model.QrType
import net.blwsmartware.qrcodescanner.qrgenerator.QrData
import net.blwsmartware.qrcodescanner.ui.result.ResultCreateActivity

class CreateWifiActivity :
    BaseActivity<ActivityCreateWifiBinding>(ActivityCreateWifiBinding::inflate) {

    override fun backPressed() {
        finish()
    }

    private val passError = mutableLiveData(" ")
    private val nameError = mutableLiveData(" ")

    override fun initialize() {
        nameError.textViewError(binding.txtErrorName, this)
        passError.textViewError(binding.txtErrorPass, this@CreateWifiActivity)
    }


    var isNextSc = false
    override fun onResume() {
        super.onResume()
        isNextSc = false
    }

    override fun ActivityCreateWifiBinding.onClick() {
        btnBack.click {
            backPressed()
        }
        btnCreate.click {
            hideKeyboard()
            if (btnCreate.isActivated && !isNextSc) {
                isNextSc = true
                val value =
                    QrData.Wifi(authSe, etName.text.toString(), etPass.text.toString(), false)
                        .encode()
                viewModel.createQrWithValue(value, QrType.EMAIL) {
                    launchActivity<ResultCreateActivity>()
                }

            }
        }

        lnSecurity.setOnClickListener {
            showCustomPopup(lnSecurity)
        }
    }

    private var authSe = QrData.Wifi.Authentication.WPA
    fun showCustomPopup(anchorView: View) {
        anchorView.post {
            val popupView = CustomPopupMenuBinding.inflate(layoutInflater, null, false)

            val popupWindow = PopupWindow(
                popupView.root,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )
            popupView.main.layoutParams.width = anchorView.width
            popupView.txt1.click {
                binding.extSecurity.text = (it as? TextView)?.text.toString()
                authSe = QrData.Wifi.Authentication.WPA
                popupWindow.dismiss()
            }
            popupView.txt2.click {
                binding.extSecurity.text = (it as? TextView)?.text.toString()
                authSe = QrData.Wifi.Authentication.WEP
                popupWindow.dismiss()
            }
            popupView.txt3.click {
                binding.extSecurity.text = (it as? TextView)?.text.toString()
                authSe = QrData.Wifi.Authentication.OPEN
                popupWindow.dismiss()
            }

            popupWindow.isOutsideTouchable = true
            popupWindow.showAsDropDown(anchorView)
        }
    }

    override fun ActivityCreateWifiBinding.setData() {
        etPass.afterTextChanged {
            if (it.trim().isEmpty()) {
                passError.set("_")
            } else if (it.length > 300) {
                passError.set(getString(R.string.maximum) + " 300 " + getString(R.string.character))
            } else if (it.length < 8) {
                passError.set(getString(R.string.require_pass_8_character))
            } else {
                passError.set("_")
            }
        }
        etName.afterTextChanged {
            if (it.isEmpty()) {
                nameError.set(getString(R.string.require_value_enter))
            } else {
                nameError.set("_")
            }
        }

        LauncherEffect(nameError, passError) {
            val passError = passError.get() == "_"
            val nameError = nameError.get() == "_"

            Log.e("TestValue", "Data is: $ $passError data: ")
            btnCreate.setState(passError && nameError)
        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        return email.matches(emailRegex.toRegex())
    }

}