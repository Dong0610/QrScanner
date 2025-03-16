package com.project.qrscanner.ui.language

import android.os.Parcelable
import com.project.qrscanner.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class LanguageModel(
    var name: String, var code: String, var active: Boolean,var flags:Int
) : Parcelable

@Parcelize
data class IntroModel(
    var title: Int = -1, var content: Int = -1, var image: Int = 0,
) : Parcelable


fun getLanguageArray(): MutableList<LanguageModel> {
    val listLanguage = mutableListOf<LanguageModel>()
    listLanguage.add(LanguageModel("English", "en", false, R.drawable.ic_lang_en))
    listLanguage.add(LanguageModel("China", "zh", false, R.drawable.ic_lang_zh))
    listLanguage.add(LanguageModel("French", "fr", false, R.drawable.ic_lang_fr))
    listLanguage.add(LanguageModel("German", "de", false,R.drawable.ic_lang_ge))
    listLanguage.add(LanguageModel("Hindi", "hi", false,R.drawable.ic_lang_hi))
    listLanguage.add(LanguageModel("Indonesia", "in", false,R.drawable.ic_lang_in))
    listLanguage.add(LanguageModel("Portuguese", "pt", false,R.drawable
        .ic_lang_pt))
    listLanguage.add(LanguageModel("Spanish", "es", false,R.drawable
        .ic_lang_es))
    return listLanguage
}

