package net.blwsmartware.qrcodescanner.dialog

import android.app.Activity
import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.base.BaseDialog
import net.blwsmartware.qrcodescanner.databinding.DialogPermissionBinding

class DialogRequestPermission(activity: Activity, var callback: () -> Unit) :
    BaseDialog<DialogPermissionBinding>(
        activity,
        DialogPermissionBinding::inflate
    ) {
    override fun DialogPermissionBinding.initView() {
        tvAgree.click {
            callback.invoke()
            dismiss()
        }
        tvStay.click {
            dismiss()
        }
    }
}