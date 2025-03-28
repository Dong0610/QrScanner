package net.blwsmartware.qrcodescanner.app


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



var isVibrate get()= sharedPreference.getBoolean("isVibrate", true)
    set (value) = sharedPreference.putBoolean("isVibrate",value)

var isSound get()= sharedPreference.getBoolean("isSound", true)
    set (value) = sharedPreference.putBoolean("isSound",value)

var finishFirstFlow = sharedPreference.getBoolean("finishFirstFlow",false)
    set(value) = sharedPreference.putBoolean("finishFirstFlow",value)

var isEmailCreateQr = sharedPreference.getBoolean("email_create_qr",false)
    set(value) = sharedPreference.putBoolean("email_create_qr",value)

var isLocationCreateQr = sharedPreference.getBoolean("localtion_create_qr",false)
    set(value) = sharedPreference.putBoolean("localtion_create_qr",value)


var isMessageCreateQr = sharedPreference.getBoolean("message_create_qr",false)
    set(value) = sharedPreference.putBoolean("message_create_qr",value)

var isPhoneCreateQr = sharedPreference.getBoolean("phone_create_qr",false)
    set(value) = sharedPreference.putBoolean("phone_create_qr",value)

var isCreateBarcode = sharedPreference.getBoolean("qr_create_barcode",false)
    set(value) = sharedPreference.putBoolean("qr_create_barcode",value)

var isUrlCreateQr = sharedPreference.getBoolean("url_create_qr",false)
    set(value) = sharedPreference.putBoolean("url_create_qr",value)





