@file:Suppress("deprecation")

package com.project.qrscanner.qrgenerator.style

import com.project.qrscanner.qrgenerator.encoder.QrCodeMatrix


interface QrLogoPadding {


    val value : Float

    val shouldApplyAccuratePadding : Boolean
    fun apply(
        matrix: QrCodeMatrix,
        logoSize: Int,
        logoPos : Int,
        logoShape: QrLogoShape
    )

    object Empty : QrLogoPadding {

        override val value: Float
            get() = 0f

        override val shouldApplyAccuratePadding: Boolean
            get() = false

        override fun apply(
            matrix: QrCodeMatrix,
            logoSize: Int,
            logoPos: Int,
            logoShape: QrLogoShape
        ) = Unit
    }
    
    data class Accurate(override val value: Float) : QrLogoPadding {

        override val shouldApplyAccuratePadding: Boolean
            get() = true

        override fun apply(
            matrix: QrCodeMatrix,
            logoSize: Int,
            logoPos: Int,
            logoShape: QrLogoShape
        ) = Unit
    }

    
    data class Natural(override val value: Float) : QrLogoPadding {

        override val shouldApplyAccuratePadding: Boolean
            get() = false

        override fun apply(
            matrix: QrCodeMatrix,
            logoSize: Int,
            logoPos : Int,
            logoShape: QrLogoShape,
        ) {
            for (x in 0 until logoSize){
                for (y in 0 until logoSize){
                    if (logoShape.invoke(x, y, logoSize, Neighbors.Empty)){
                        matrix[logoPos+x, logoPos+y] =
                            QrCodeMatrix.PixelType.Logo
                    }
                }
            }
        }
    }
}
