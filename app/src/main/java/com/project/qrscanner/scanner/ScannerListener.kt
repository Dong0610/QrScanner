package com.project.qrscanner.scanner

interface ScannerListener {
    fun onScanResult(result: String)
    fun onScanFailed()
}