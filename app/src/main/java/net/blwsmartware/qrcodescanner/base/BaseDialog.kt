package net.blwsmartware.qrcodescanner.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import net.blwsmartware.qrcodescanner.R

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.graphics.drawable.toDrawable


abstract class BaseDialog<V : ViewBinding>(context: Context,   val bindingFactory: (LayoutInflater) -> V, var cancelAble: Boolean = false) :
    Dialog(context, com.dong.baselib.R.style.BaseDialog) {
    private val TAG: String = BaseDialog::class.java.name
    val binding: V by lazy { bindingFactory(layoutInflater) }
    protected abstract fun V.initView()

    init {
        initialize()
    }


    open fun initialize() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setGravity(Gravity.CENTER)
        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    open fun showKeyboard(view: View?) {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    open fun showKeyboard() {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.root, InputMethodManager.SHOW_IMPLICIT)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.initView()
        setCancelable(cancelAble)
        this.setCanceledOnTouchOutside(cancelAble)
    }

    @Override
    override fun show() {
        if(isShowing){
            dismiss()
        }
        super.show()

    }

}