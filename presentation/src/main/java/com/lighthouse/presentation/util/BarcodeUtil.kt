package com.lighthouse.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.ColorInt
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import com.lighthouse.presentation.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BarcodeUtil @Inject constructor(@ApplicationContext val context: Context) {
    fun displayBitmap(imageView: ImageView, value: String) {
        val widthPixels = context.resources.getDimensionPixelSize(R.dimen.width_barcode)
        val heightPixels = context.resources.getDimensionPixelSize(R.dimen.height_barcode)

        imageView.setImageBitmap(
            createBarcodeBitmap(
                barcodeValue = value,
                widthPixels = widthPixels,
                heightPixels = heightPixels,
                barcodeColor = context.getColor(R.color.black),
                backgroundColor = context.getColor(android.R.color.white)
            )
        )
    }

    fun createBarcodeBitmap(
        barcodeValue: String,
        widthPixels: Int,
        heightPixels: Int,
        @ColorInt barcodeColor: Int = context.getColor(R.color.black),
        @ColorInt backgroundColor: Int = context.getColor(android.R.color.white)
    ): Bitmap {
        val bitMatrix = Code128Writer().encode(
            barcodeValue,
            BarcodeFormat.CODE_128,
            widthPixels,
            heightPixels
        )

        val pixels = IntArray(bitMatrix.width * bitMatrix.height)
        for (y in 0 until bitMatrix.height) {
            val offset = y * bitMatrix.width
            for (x in 0 until bitMatrix.width) {
                pixels[offset + x] =
                    if (bitMatrix.get(x, y)) barcodeColor else backgroundColor
            }
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
