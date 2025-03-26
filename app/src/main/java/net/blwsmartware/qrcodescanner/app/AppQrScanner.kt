package net.blwsmartware.qrcodescanner.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.Toast
import com.dong.baselib.permission.Permission
import com.dong.baselib.system.SharedPreference
import io.sad.monster.dialog.AppPurchase


class AppQrScanner : Application() {

    companion object {
        lateinit var sharedPreference: SharedPreference

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        @SuppressLint("StaticFieldLeak")
        var permission = Permission()
        lateinit var viewModel: AppViewModel
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        sharedPreference = SharedPreference(this)
        viewModel = AppViewModel(this@AppQrScanner)
        permission.initialize(this)
    }
}

var sharedPreference = AppQrScanner.sharedPreference
    get() = AppQrScanner.sharedPreference
    set(value) {
        field = value
    }

val permission get() = AppQrScanner.permission
val context get() = AppQrScanner.context
val viewModel get() = AppQrScanner.viewModel
var toast: String = ""
    set(value) {
        field = value
        Toast.makeText(context, value, Toast.LENGTH_SHORT).show()
    }























