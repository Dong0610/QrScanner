package net.blwsmartware.qrcodescanner.dialog

import android.app.Activity
import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.base.BaseDialog
import net.blwsmartware.qrcodescanner.databinding.DeleteDialogBinding

class DeleteDialog( activity:Activity, var action:()->Unit): BaseDialog<DeleteDialogBinding>(activity,DeleteDialogBinding::inflate) {
    override fun DeleteDialogBinding.initView() {
        tvStay.click {
            dismiss()
        }
        tvAgree.click {
            action()
            dismiss()
        }
    }
}