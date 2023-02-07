package com.lighthouse.core.android.utils.barcode

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.ColorInt
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import com.lighthouse.beep.core.R

class BarcodeUtil {
    fun createBarcodeBitmap(
        context: Context,
        barcodeValue: String,
        widthPixels: Int = context.resources.getDimensionPixelSize(R.dimen.width_barcode),
        heightPixels: Int = context.resources.getDimensionPixelSize(R.dimen.height_barcode),
        @ColorInt barcodeColor: Int = context.getColor(R.color.black),
        @ColorInt backgroundColor: Int = context.getColor(android.R.color.white)
    ): Bitmap {
        val bitMatrix = Code128Writer().encode(
            barcodeValue,
            BarcodeFormat.CODE_128,
            widthPixels,
            heightPixels
        )

        val pixels = IntArray(bitMatrix.width * bitMatrix.height) { i ->
            val y = i / bitMatrix.width
            val x = i % bitMatrix.width
            if (bitMatrix.get(x, y)) barcodeColor else backgroundColor
        }

        val bitmap = Bitmap.createBitmap(
            bitMatrix.width,
            bitMatrix.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels(
            pixels,
            0,
            bitMatrix.width,
            0,
            0,
            bitMatrix.width,
            bitMatrix.height
        )
        return bitmap
    }
}
