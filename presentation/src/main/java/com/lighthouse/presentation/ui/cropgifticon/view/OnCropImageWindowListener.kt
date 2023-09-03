package com.lighthouse.presentation.ui.cropgifticon.view

import android.graphics.RectF

interface OnCropImageWindowListener {

    fun onWindowMove(unconsumedX: Float, unconsumedY: Float, boundRect: RectF)

    fun onWindowResized()

    fun onWindowTouchComplete(curCropRectF: RectF)
}
