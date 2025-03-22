package net.blwsmartware.qrcodescanner.ui.create

import BarcodeType
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.dong.baselib.lifecycle.LauncherEffect
import com.dong.baselib.lifecycle.change
import com.dong.baselib.lifecycle.get
import com.dong.baselib.lifecycle.mutableLiveData
import com.dong.baselib.lifecycle.set
import com.dong.baselib.widget.afterTextChanged
import com.dong.baselib.widget.click
import com.dong.baselib.widget.invisible
import com.dong.baselib.widget.visible
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.app.viewModel
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.base.BaseAdapter
import net.blwsmartware.qrcodescanner.builder.setState
import net.blwsmartware.qrcodescanner.builder.textViewError
import net.blwsmartware.qrcodescanner.databinding.ActivityCreateBarcodeBinding
import net.blwsmartware.qrcodescanner.databinding.CustomPopupTypeBinding
import net.blwsmartware.qrcodescanner.databinding.ItemSelectTypeBinding
import net.blwsmartware.qrcodescanner.ui.result.ResultCreateActivity

class CreateBarCodeActivity :
    BaseActivity<ActivityCreateBarcodeBinding>(ActivityCreateBarcodeBinding::inflate) {
    override fun backPressed() {
        finish()
    }

    override fun initialize() {
        erroInput.textViewError(binding.txtError, this)
    }

    private var erroInput = mutableLiveData(" ")

    inner class BarcodeTypeAdapter(var callback: (BarcodeType) -> Unit) :
        BaseAdapter<BarcodeType, ItemSelectTypeBinding>() {
        override fun createBinding(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemSelectTypeBinding.inflate(layoutInflater, parent, false)

        override fun ItemSelectTypeBinding.bind(item: BarcodeType, position: Int) {
            currentPosition.change {
                if (it == position) {
                    icCheck.visible()
                } else {
                    icCheck.invisible()
                }
            }
            txt2.text = item.format.name + " (${item.length})"
            root.click {
                callback(item)
                currentPosition.value = position
                popupWindow?.dismiss()
            }
        }

    }

    private var currentType = mutableLiveData(BarcodeType.EAN_8)

    private var barcodeTypeAdapter = BarcodeTypeAdapter() {
        currentType.value = it
    }

    private var popupWindow: PopupWindow? = null
    fun intiPopup(anchorView: View) {
        anchorView.post {
            val popupView = CustomPopupTypeBinding.inflate(layoutInflater, null, false)
            popupWindow = PopupWindow(
                popupView.root,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )
            popupView.main.layoutParams.width = anchorView.width
            popupView.rctItem.adapter = barcodeTypeAdapter
            barcodeTypeAdapter.currentPosition?.value = 2
            popupWindow?.isOutsideTouchable = true
        }
    }

    private var isForcus = false
    private var isForcusText=false

    override fun ActivityCreateBarcodeBinding.onClick() {
        btnBack.click {
            backPressed()
        }

        lnSecurity.click {
            popupWindow?.showAsDropDown(lnSecurity)
        }
        currentType.change {
            edtPhone.filters = arrayOf(InputFilter.LengthFilter(it.length))
            extSecurity.text = it.format.name + " (${it.length})"
            if (!isForcus) {
                isForcus = true
                val currentValue = edtPhone.text.toString().trim().replace(" ", "")
                if(isForcusText){
                    if (currentValue.isEmpty()) {
                        erroInput.set(getString(R.string.please_fill_outline))
                    } else if (currentValue.length > it.length) {
                        erroInput.set(getString(R.string.require) + " ${it.length} " + getString(R.string.character))
                    } else {
                        erroInput.set("_")
                    }
                }

            }

        }

        btnCreate.click {
            hideKeyboard()
            if(btnCreate.isActivated){
                viewModel.createBarCodeWithValue(edtPhone.text.toString(),currentType.get()!!){
                    launchActivity<ResultCreateActivity>()
                }
            }
        }

        LauncherEffect(erroInput) {
            btnCreate.setState(erroInput.get() == "_")
        }
        edtPhone.setOnFocusChangeListener { _, hasFocus ->
            isForcusText=true
        }
    }

    override fun ActivityCreateBarcodeBinding.setData() {
        intiPopup(lnSecurity)

        barcodeTypeAdapter.submitList(BarcodeType.entries)

        edtPhone.afterTextChanged {
            val currentValue = it.toString().trim().replace(" ", "")
            if (it.isNullOrBlank()) {
                erroInput.set(getString(R.string.please_fill_outline))
            } else if (currentValue.length == currentType.get()!!.length) {
                erroInput.set("_")
            } else {
                erroInput.set(
                    getString(R.string.require) + " ${currentType.value?.length ?: 0}" + getString(
                        R.string.character
                    )
                )

            }
        }

    }
}