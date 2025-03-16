@file:Suppress("DEPRECATION")

package com.project.qrscanner.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.widget.Toast
import com.project.qrscanner.R
import com.project.qrscanner.builder.reconvertUnicodeToEmoji
import java.io.Serializable


class WifiModel(var name: String = "", var pass: String = "", var security: String = "") :
    Serializable

fun parseWifiString(wifiString: String): WifiModel? {
    if (!wifiString.startsWith("WIFI:")) return null

    val wifiParams = wifiString.removePrefix("WIFI:").split(";")
    var name = ""
    var pass = ""
    var security = ""

    wifiParams.forEach { param ->
        when {
            param.startsWith("S:") -> name = param.removePrefix("S:")
            param.startsWith("P:") -> pass = param.removePrefix("P:")
            param.startsWith("T:") -> security = param.removePrefix("T:")
        }
    }

    return WifiModel(name.reconvertUnicodeToEmoji(), pass.reconvertUnicodeToEmoji(), security)
}

fun connectToWifi(context: Context,password:String,ssid:String){
    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val suggestion = WifiNetworkSuggestion.Builder()
            .setSsid(ssid) // Set SSID
            .setWpa2Passphrase(password) // Set WPA2 password
            .build()

        val suggestionsList = listOf(suggestion)
        val status = wifiManager.addNetworkSuggestions(suggestionsList)

        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            Toast.makeText(
                context,
                context.getString(R.string.connect_to) + ssid,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.faild_connect_to) + " $ssid",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    else{
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiConfig = WifiConfiguration().apply {
            SSID = String.format("\"%s\"", ssid) // Wrap SSID in quotes
            preSharedKey = String.format("\"%s\"", password) // Wrap password in quotes
        }

        val netId = wifiManager.addNetwork(wifiConfig)
        if (netId != -1) {
            wifiManager.disconnect()
            wifiManager.enableNetwork(netId, true)
            wifiManager.reconnect()
            Toast.makeText(
                context,
                context.getString(R.string.connect_to) + ssid,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.faild_connect_to) + " $ssid",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

fun connectToWifi(context: Context, value: String) {
    val wifiInfo = parseWifiString(value)
    if (wifiInfo == null) return
    else {
        val ssid = wifiInfo.name
        val password = wifiInfo.pass

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build()

            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(wifiNetworkSpecifier)
                .build()

            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.requestNetwork(
                networkRequest,
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        connectivityManager.bindProcessToNetwork(network)
                        Toast.makeText(
                            context,
                            context.getString(R.string.connect_to) + ssid,
                            Toast.LENGTH_SHORT
                       ).show()
                    }

                    override fun onUnavailable() {
                        Toast.makeText(
                            context,
                            context.getString(R.string.faild_connect_to) + " $ssid",
                            Toast.LENGTH_SHORT
                       ).show()
                    }
                })
        } else {
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiConfig = WifiConfiguration().apply {
                SSID = "\"$ssid\""
                preSharedKey = "\"$password\""
            }

            val netId = wifiManager.addNetwork(wifiConfig)
            wifiManager.disconnect()
            wifiManager.enableNetwork(netId, true)
            wifiManager.reconnect()
            Toast.makeText(
                context,
                context.getString(R.string.attemp_value) + ssid,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
}
