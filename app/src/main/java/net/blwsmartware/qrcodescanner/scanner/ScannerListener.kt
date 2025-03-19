package net.blwsmartware.qrcodescanner.scanner

interface ScannerListener {
    fun onScanResult(result: String)
    fun onScanFailed()
}