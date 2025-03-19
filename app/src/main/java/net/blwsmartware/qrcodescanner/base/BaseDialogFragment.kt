package net.blwsmartware.qrcodescanner.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import net.blwsmartware.qrcodescanner.R
import com.dong.baselib.api.setFullScreen

abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {
    private lateinit var myContext: Context

    open fun isFullWidth(): Boolean = true
    open fun isFullSize(): Boolean = false

    open fun isCancelableDialog(): Boolean = true
    open fun isCanceledOnTouchOutside(): Boolean = true

    protected lateinit var binding: VB
        private set

    protected abstract fun inflateBinding(inflater: LayoutInflater): VB

    abstract fun updateUI(savedInstanceState: Bundle?)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.myContext = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = RelativeLayout(activity)
        root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        val dialog = if (isFullSize()) Dialog(
            activity as FragmentActivity,
            com.dong.baselib.R.style.AppTheme_DialogFragmentFullScreen
        ) else Dialog(activity as FragmentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside())
        dialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setLayout(
                if (isFullWidth() || isFullSize()) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT,
                if (isFullSize()) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            if (isFullSize()) {
                it.setFullScreen()
                val context = context
                if (context != null) {
                    val colorStatusBar = ContextCompat.getColor(context, getStatusBarColor())
                    it.statusBarColor = colorStatusBar
                    val windowInsetsController = WindowCompat.getInsetsController(it, it.decorView)
                    windowInsetsController.isAppearanceLightStatusBars = isColorLight(colorStatusBar)
                }
            }
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflateBinding(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = isCancelableDialog()
        updateUI(savedInstanceState)
    }

    @ColorRes
    open fun getStatusBarColor(): Int {
        return com.dong.baselib.R.color.color_status_bar
    }

    private fun isColorDark(@ColorInt color: Int): Boolean {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

    private fun isColorLight(@ColorInt color: Int): Boolean {
        return !isColorDark(color)
    }

    fun show(fm: FragmentManager) = apply {
        show(fm, this::class.java.canonicalName)
    }


    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            manager.beginTransaction().add(this@BaseDialogFragment, tag).commitAllowingStateLoss()
        }
    }

    fun getContextF(): Context {
        return context ?: activity ?: myContext
    }
}
