package com.lighthouse.presentation.ui.cropgifticon

import android.content.Intent
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import com.lighthouse.presentation.ui.cropgifticon.view.CropImageInfo
import com.lighthouse.presentation.ui.cropgifticon.view.CropImageMode

data class CropGifticonParams(
    val cropInfo: CropImageInfo,
    val cropImageMode: CropImageMode = CropImageMode.DRAG_WINDOW,
    val enableAspectRatio: Boolean = true,
    val aspectRatio: Float = 1f,
) {
    fun buildBundle() = Bundle().apply {
        putParcelable(KEY_ORIGIN_IMAGE, cropInfo.uri)
        putParcelable(KEY_CROPPED_RECT, cropInfo.croppedRect)
        putString(KEY_CROP_IMAGE_MODE, cropImageMode.name)
        putBoolean(KEY_ENABLE_ASPECT_RATIO, enableAspectRatio)
        putFloat(KEY_ASPECT_RATIO, aspectRatio)
    }

    fun putExtra(intent: Intent) {
        intent.apply {
            putExtra(KEY_ORIGIN_IMAGE, cropInfo.uri)
            putExtra(KEY_CROPPED_RECT, cropInfo.croppedRect)
//            putExtra(KEY_CROP_IMAGE_MODE, cropImageMode.name)
            putExtra(KEY_CROP_IMAGE_MODE, CropImageMode.DRAG_WINDOW.name)
            putExtra(KEY_ENABLE_ASPECT_RATIO, enableAspectRatio)
            putExtra(KEY_ASPECT_RATIO, aspectRatio)
        }
    }

    companion object {
        private const val KEY_ORIGIN_IMAGE = "Key.OriginImage"
        private const val KEY_CROPPED_RECT = "Key.CroppedRect"
        private const val KEY_CROP_IMAGE_MODE = "Key.CropImageMode"
        private const val KEY_ENABLE_ASPECT_RATIO = "Key.EnableAspectRatio"
        private const val KEY_ASPECT_RATIO = "Key.AspectRatio"

        fun getCropInfo(savedStateHandle: SavedStateHandle): CropImageInfo {
            return CropImageInfo(
                savedStateHandle.get<Uri>(KEY_ORIGIN_IMAGE),
                savedStateHandle.get<RectF>(KEY_CROPPED_RECT),
            )
        }

        fun getCropImageMode(savedStateHandle: SavedStateHandle): CropImageMode {
            val name = savedStateHandle.get<String>(KEY_CROP_IMAGE_MODE)
            return getCropImageMode(name)
        }

        private fun getCropImageMode(name: String?): CropImageMode {
            return name?.let {
                runCatching {
                    CropImageMode.valueOf(it)
                }.getOrDefault(CropImageMode.DRAG_WINDOW)
            } ?: CropImageMode.DRAG_WINDOW
        }

        fun getEnableAspectRatio(savedStateHandle: SavedStateHandle): Boolean {
            return savedStateHandle.get<Boolean>(KEY_ENABLE_ASPECT_RATIO) ?: true
        }

        fun getAspectRatio(savedStateHandle: SavedStateHandle): Float {
            return savedStateHandle.get<Float>(KEY_ASPECT_RATIO) ?: 1f
        }
    }
}
