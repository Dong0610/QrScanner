package net.blwsmartware.qrcodescanner.ui.create

import com.dong.baselib.lifecycle.LauncherEffect
import com.dong.baselib.lifecycle.get
import com.dong.baselib.lifecycle.mutableLiveData
import com.dong.baselib.string.isEmail
import com.dong.baselib.widget.afterTextChanged
import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.app.viewModel
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.builder.setState
import net.blwsmartware.qrcodescanner.builder.textViewError
import net.blwsmartware.qrcodescanner.databinding.ActivityCreateContactBinding
import net.blwsmartware.qrcodescanner.model.ContactModel
import net.blwsmartware.qrcodescanner.model.QrType
import net.blwsmartware.qrcodescanner.model.toVCard
import net.blwsmartware.qrcodescanner.ui.result.ResultCreateActivity

class CreateContactActivity :
    BaseActivity<ActivityCreateContactBinding>(ActivityCreateContactBinding::inflate) {
    override fun backPressed() {
        finish()
    }

    var nameError = mutableLiveData(" ")
    var phoneError = mutableLiveData(" ")
    var emailError = mutableLiveData(" ")
    var addressError = mutableLiveData(" ")
    var companyError = mutableLiveData(" ")
    var descriptionError = mutableLiveData(" ")

    override fun initialize() {
        nameError.textViewError(binding.txtErrorName, this)
        phoneError.textViewError(binding.txtErrorPhone, this)
        emailError.textViewError(binding.txtErrorEmail, this)
        addressError.textViewError(binding.txtErrorAddress, this)
        companyError.textViewError(binding.txtErrorCompany, this)
        descriptionError.textViewError(binding.txtErrorDescription, this)
    }

    private var isCreate = false
    override fun onResume() {
        super.onResume()
        isCreate = false
    }

    override fun <T> fragmentSendData(key: String, data: T) {
        super.fragmentSendData(key, data)
        if (key == "contact") {
            (data as? ContactModel)?.let {
                isCreate = true
                binding.etName.setText(it.name)
                binding.etPhone.setText(it.phone)
                binding.etEmail.setText(it.email)
                binding.etAdress.setText(it.address)
                binding.etCompany.setText(it.company)
                binding.etNote.setText(it.note)
            }
        }

    }

    override fun ActivityCreateContactBinding.onClick() {
        btnBack.click {
            backPressed()
        }
        btnCreate.click {
            if (!isCreate && it?.isActivated == true) {
                val contactModel = ContactModel(
                    etName.text.toString(),
                    etCompany.text.toString(),
                    etPhone.text.toString(),
                    etEmail.text.toString(),
                    etNote.text.toString(),
                    etAdress.text.toString()
                ).toVCard()
                viewModel.createQrWithValue(contactModel,QrType.CONTACT){
                    launchActivity<ResultCreateActivity>()
                }
            }
        }
        txtChoose.click {
            addFragment(SelectContactFragment())
        }

    }

    override fun ActivityCreateContactBinding.setData() {
        etName.afterTextChanged {
            nameError.value =
                if (it.trim()
                        .isEmpty()
                ) getString(R.string.please_fill_outline) else if (it.length > 100) getString(R.string.maximum) + "100" + getString(
                    R.string.character
                ) else "_"
        }
        etPhone.afterTextChanged {
            phoneError.value = if (it.length > 15) getString(R.string.maximum) + "15" + getString(
                    R.string.character
                ) else "_"
        }
        etEmail.afterTextChanged {
            emailError.value =if(it.replace(" ","")!="") {
                if (!it.isEmail()) getString(R.string.invalid_email_format) else "_"
            }else{
                "_"
            }
        }
        etAdress.afterTextChanged {
            addressError.value = if (it.length > 300) getString(R.string.maximum) + "300" + getString(
                    R.string.character
                ) else "_"
        }
        etCompany.afterTextChanged {
            companyError.value = if (it.length > 300) getString(R.string.maximum) + "300" + getString(
                    R.string.character
                ) else "_"
        }
        etNote.afterTextChanged { if (it.length > 200) getString(
                    R.string.maximum
                ) + "200" + getString(R.string.character) else "_"
        }

        LauncherEffect(
            nameError,
            phoneError,
            emailError,
            addressError,
            companyError,
            descriptionError
        ) {
            val error1 = nameError.get() == "_"
            val error2 = phoneError.get() == "_"
            val error3 = emailError.get() == "_"
            val error4 = addressError.get() == "_"
            val error5 = companyError.get() == "_"
            val error6 = descriptionError.get() == "_"
            btnCreate.setState(error1 && error2 && error3 && error4 && error5 && error6)
        }
    }
}