package com.project.qrscanner.dialog

import android.content.Context
import android.util.Log
import com.dong.baselib.api.isApi33orHigher
import com.dong.baselib.widget.click
import com.project.qrscanner.R
import com.project.qrscanner.base.BaseActivity
import com.project.qrscanner.base.BaseDialog
import com.project.qrscanner.databinding.DialogGotoSettingBinding

interface OnGotoSetting {
    fun onAgree()
    fun onDeny()
}

class DialogGotoSetting(
    private var typeGoSettings: BaseActivity.TypeGoSettings,
    context: Context,
    cancellable: Boolean = false,
    private var onCallback: OnGotoSetting,
) : BaseDialog<DialogGotoSettingBinding>(context, cancellable) {
    override fun setBinding(): DialogGotoSettingBinding {
        return DialogGotoSettingBinding.inflate(layoutInflater)
    }

    override fun DialogGotoSettingBinding.initView() {

        binding.tvStay.click {
            onCallback.onDeny()
            dismiss()
        }
        binding.tvAgree.click {
            onCallback.onAgree()
            dismiss()
        }
        val text = when (typeGoSettings) {
            BaseActivity.TypeGoSettings.CAMERA -> context.getString(R.string.content_dialog_camera)
            BaseActivity.TypeGoSettings.STORAGE -> if (isApi33orHigher) {
                context.getString(R.string.content_dialog_per_storage_33_after)
            } else {
                context.getString(R.string.content_dialog_per_storage_33_before)
            }
            BaseActivity.TypeGoSettings.NOTIFICATION -> context.getString(R.string.content_dialog_per_noti)
            BaseActivity.TypeGoSettings.CONTACT -> context.getString(R.string.content_dialog_contact)
            BaseActivity.TypeGoSettings.LOCATION -> context.getString(R.string.content_dialog_location)
            else -> ""
        }
        binding.tvContent.text = text
    }

}
