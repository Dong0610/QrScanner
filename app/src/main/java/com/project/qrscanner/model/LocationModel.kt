package com.project.qrscanner.model

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import com.project.qrscanner.R
import com.project.qrscanner.app.toastShort
import java.io.Serializable

class LocationModel(
    var longitude: String = "",
    var latitude: String = "",
    var query: String = ""
) : Serializable {
    override fun toString(): String {
        return "GEO:$longitude,$latitude?q=$query"
    }
    companion object {
        fun fromString(geoString: String): LocationModel? {
            try {

                if (!geoString.startsWith("GEO:")) return null
                val parts = geoString.removePrefix("GEO: ").split("?q=")

                if (parts.size != 2) return null
                val coordinates = parts[0].split(",")
                if (coordinates.size != 2) return null
                return LocationModel(coordinates[0], coordinates[1], parts[1])
            } catch (e: Exception) {
                return null
            }
        }
    }
}

object MapUtils {
    sealed class MapApp {
        object GoogleMaps : MapApp()
        object Browser : MapApp()
        data class Other(val packageName: String) : MapApp()
    }

    fun openMap(
        context: Context,
        location: LocationModel,
        preferredApp: MapApp = MapApp.GoogleMaps,
        showChooser: Boolean = false
    ) {
        try {
            val intent = createMapIntent(location, preferredApp)

            when {
                showChooser -> {
                    val chooser = Intent.createChooser(
                        intent,
                        context.getString(R.string.open_with)
                    )
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(chooser)
                    } else {
                        openInBrowser(context, location)
                    }
                }
                intent.resolveActivity(context.packageManager) != null -> {
                    context.startActivity(intent)
                }
                else -> {
                    openInBrowser(context, location)
                }
            }
        } catch (e: Exception) {
            Log.e("MapUtils", "Error opening map", e)
            context.toastShort(context.getString(R.string.error_opening_map))
        }
    }

    private fun createMapIntent(location: LocationModel, mapApp: MapApp): Intent {
        val intent = when {
            location.latitude.isNotEmpty() && location.longitude.isNotEmpty() -> {
                Intent(Intent.ACTION_VIEW, Uri.parse(
                    "geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}"
                ))
            }
            location.query.isNotEmpty() -> {
                Intent(Intent.ACTION_VIEW, Uri.parse(
                    "geo:0,0?q=${Uri.encode(location.query)}"
                ))
            }
            else -> throw IllegalArgumentException("Invalid location data")
        }

        when (mapApp) {
            is MapApp.GoogleMaps -> intent.setPackage("com.google.android.apps.maps")
            is MapApp.Other -> intent.setPackage(mapApp.packageName)
            is MapApp.Browser -> {}
        }

        return intent
    }

    private fun openInBrowser(context: Context, location: LocationModel) {
        val url = when {
            location.latitude.isNotEmpty() && location.longitude.isNotEmpty() ->
                "https://www.google.com/maps?q=${location.latitude},${location.longitude}"
            location.query.isNotEmpty() ->
                "https://www.google.com/maps?q=${Uri.encode(location.query)}"
            else -> return
        }

        try {
            CustomTabsIntent.Builder()
                .build()
                .launchUrl(context, Uri.parse(url))
        } catch (e: Exception) {
            // Fallback to regular browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                context.toastShort(context.getString(R.string.error_opening_map))
            }
        }
    }

    fun getAvailableMapApps(context: Context): List<MapApp> {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=test"))
        return context.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .map { MapApp.Other(it.activityInfo.packageName) }
            .toList()
    }
}