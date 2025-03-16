@file:Suppress("DEPRECATION")

package com.project.qrscanner.base

import android.content.Context
import android.content.res.Configuration
import java.util.Locale


object SystemUtil {
    var myLocale: Locale? = null
    fun saveLocale(context: Context, lang: String?) {
        setPreLanguage(context, lang)
    }
     fun setLocale(context: Context, language: String = "en") {
        if (language.isEmpty()) {
            val config = Configuration()
            val locale = context.resources.configuration.locale
            Locale.setDefault(locale)
            config.locale = locale
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        } else {
            changeLang(language, context)
        }
    }


    private fun changeLang(lang: String?, context: Context) {
        if (lang.isNullOrEmpty()) return
        val parts = lang.split("_")
        val languageCode = parts[0]
        val countryCode = if (parts.size > 1) parts[1] else ""
        myLocale = if (countryCode.isNotEmpty()) {
            Locale(languageCode, countryCode)
        } else {
            Locale(languageCode)
        }
        saveLocale(context, lang ?: "en")
        Locale.setDefault(myLocale)
        val config = Configuration()
        config.locale = myLocale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }


    fun getPreLanguage(mContext: Context): String? {
        val preferences = mContext.getSharedPreferences("data", Context.MODE_PRIVATE)
        return preferences.getString("KEY_LANGUAGE", "en")
    }

    private fun setPreLanguage(context: Context, language: String?) {
        if (language == null || language == "") {
        } else {
            val preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString("KEY_LANGUAGE", language)
            editor.apply()
        }
    }

    fun forceRated(context: Context) {
        val preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("rated", true)
        editor.apply()
    }

    fun isRatting(context: Context): Boolean {
        val preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        return preferences.getBoolean("rated", false)
    }
}
