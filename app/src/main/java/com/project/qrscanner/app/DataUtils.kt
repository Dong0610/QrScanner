package com.project.qrscanner.app


var countGrantNoti
    get() = sharedPreference.getInt("countGrantNoti", 0)
    set(value) = sharedPreference.putInt("countGrantNoti", value)
var countGrantFile
    get() = sharedPreference.getInt("countGrantFile", 0)
    set(value) = sharedPreference.putInt("countGrantFile", value)
var countGrantCamera
    get() = sharedPreference.getInt("countGrantCamera", 0)
    set(value) = sharedPreference.putInt("countGrantCamera", value)
var countGrantLocation
    get() = sharedPreference.getInt("countGrantLocation", 0)
    set(value) = sharedPreference.putInt("countGrantLocation", value)
var countGrantContact
    get() = sharedPreference.getInt("countGrantContact", 0)
    set(value) = sharedPreference.putInt("countGrantContact", value)
var countGrantWriteSetting
    get() = sharedPreference.getInt("countGrantWriteSetting", 0)
    set(value) = sharedPreference.putInt("countGrantWriteSetting", value)

var currentBrokenValue get()= sharedPreference.getString("currentBrokenValue","")
    set(value) = sharedPreference.putString("currentBrokenValue", value)


var firstOpenApp
    get() = sharedPreference.getInt("firstOpenApp", 0)
    set(value) = sharedPreference.putInt("firstOpenApp", value)
var isSelectHand get() = sharedPreference.getBoolean("isSelectHand", false)
set(value) = sharedPreference.putBoolean("isSelectHand",value)

var isChooseLanguage get()= sharedPreference.getBoolean("isChooseLanguage", false)
    set(value) = sharedPreference.putBoolean("isChooseLanguage", value)

var isShowIntro get()= sharedPreference.getBoolean("isShowIntro", false)
    set(value) = sharedPreference.putBoolean("isShowIntro", value)







