package net.blwsmartware.qrcodescanner.dialog

import android.app.Activity
import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.base.BaseDialog
import net.blwsmartware.qrcodescanner.databinding.DialogPermissionBinding
import net.blwsmartware.qrcodescanner.databinding.DialogQuitAppBinding

class DialogQuitApp(activity:Activity,var callback: ()->Unit) : BaseDialog<DialogQuitAppBinding>(activity,DialogQuitAppBinding::inflate){
    override fun DialogQuitAppBinding.initView() {
        tvAgree.click {
            callback.invoke()
            dismiss()
        }
        tvStay.click {
            dismiss()
        }
    }
}