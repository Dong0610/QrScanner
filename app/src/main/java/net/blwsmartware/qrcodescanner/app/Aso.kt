package net.blwsmartware.qrcodescanner.app

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import net.blwsmartware.qrcodescanner.R


fun Context.openUrl(url: String) {
    runCatching {
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).also {
            this.startActivity(it)
        }
    }
}

fun Context.toastShort(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.rateApp(onFinish: () -> Unit) {
    val manager = ReviewManagerFactory.create(this)
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task: Task<ReviewInfo> ->
        if (task.isSuccessful) {
            val reviewInfo = task.result
            val flow =
                manager.launchReviewFlow(this, reviewInfo)
            flow.addOnCompleteListener {
                Log.d("RateApp", "Rate complete")
                onFinish()
            }
        } else {
            Log.e("RateApp", "error: " + task.exception.toString())
            onFinish()
        }
    }
}

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun Context.isInternetAvailable(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

fun Context.shareApp() {
    val applicationID = this.packageName
    val appPlayStoreUrl = "https://play.google.com/store/apps/details?id=$applicationID"

    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.app_name))
    shareIntent.putExtra(
        Intent.EXTRA_TEXT,
        getString(R.string.check_out_this_amazing_app) + appPlayStoreUrl
    )

    val chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_via))
    chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(chooserIntent)
}
