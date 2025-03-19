package net.blwsmartware.qrcodescanner.builder

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import net.blwsmartware.qrcodescanner.R

object UrlUtils {
    fun openUrl(
        context: Context,
        url: String,
        config: UrlOpenConfig = UrlOpenConfig()
    ) {
        try {
            when (config.openType) {
                OpenType.CUSTOM_TABS -> openWithCustomTabs(context, url, config)
                OpenType.BROWSER -> openWithBrowser(context, url)
                OpenType.CHOOSER -> openWithChooser(context, url, config.chooserTitle)
            }
        } catch (e: Exception) {
            Log.e("UrlUtils", "Error opening URL", e)
            config.errorCallback?.invoke(e)
            Toast.makeText(
                context, 
                config.errorMessage ?: context.getString(R.string.error_opening_url),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openWithCustomTabs(
        context: Context,
        url: String,
        config: UrlOpenConfig
    ) {
        try {
            CustomTabsIntent.Builder().apply {
                config.toolbarColor?.let { setToolbarColor(it) }
                setShowTitle(true)
                setUrlBarHidingEnabled(true)
                if (config.showShareMenuItem) {
                    setShareState(CustomTabsIntent.SHARE_STATE_ON)
                }
            }.build().launchUrl(context, Uri.parse(url))
        } catch (e: Exception) {
            if (config.fallbackToBrowser) {
                openWithBrowser(context, url)
            } else {
                throw e
            }
        }
    }

    private fun openWithBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            throw ActivityNotFoundException("No browser found")
        }
    }

    private fun openWithChooser(context: Context, url: String, chooserTitle: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val chooser = Intent.createChooser(intent, chooserTitle)
        if (chooser.resolveActivity(context.packageManager) != null) {
            context.startActivity(chooser)
        } else {
            throw ActivityNotFoundException("No apps found to handle URL")
        }
    }

    data class UrlOpenConfig(
        val openType: OpenType = OpenType.CUSTOM_TABS,
        val toolbarColor: Int? = null,
        val showShareMenuItem: Boolean = true,
        val fallbackToBrowser: Boolean = true,
        val chooserTitle: String? = null,
        val errorMessage: String? = null,
        val errorCallback: ((Exception) -> Unit)? = null
    )

    enum class OpenType {
        CUSTOM_TABS,
        BROWSER,
        CHOOSER
    }
}