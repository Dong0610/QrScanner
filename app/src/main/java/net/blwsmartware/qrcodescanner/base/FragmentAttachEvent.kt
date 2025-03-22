package net.blwsmartware.qrcodescanner.base

interface FragmentAttachEvent {
    fun fragmentOnBack(){}
    fun fragmentAction(data:(Any)->Unit={}){}
    fun <T>fragmentSendData(key:String="",data:(T)){}
}