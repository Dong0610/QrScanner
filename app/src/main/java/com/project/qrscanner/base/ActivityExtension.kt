package com.project.qrscanner.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


inline fun <reified T : Any> Context.launchActivity(
    params: HashMap<String, Any>? = null,
) {
    val intent = newIntent<T>(this)
    val bundle = Bundle()
    params?.let { map ->

        for ((key, value) in map) {
            when (value) {
                is Int -> bundle.putInt(key, value)
                is String -> bundle.putString(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                is Float -> bundle.putFloat(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Char -> bundle.putChar(key, value)
                is CharSequence -> bundle.putCharSequence(key, value)
                is Bundle -> bundle.putBundle(key, value)
                // Add more types as needed
                else -> throw IllegalArgumentException("Unsupported bundle component (${value.javaClass})")
            }
        }
        intent.putExtras(bundle)
    }
    startActivity(intent, bundle)
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)