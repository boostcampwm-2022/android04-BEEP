package com.lighthouse.presentation.ui.edit.event

enum class EditGifticonTag(val needKeyboard: Boolean) {
    GIFTICON_NAME(true),
    BRAND_NAME(true),
    APPROVE_BRAND_NAME(false),
    BARCODE(true),
    BALANCE(true),
    APPROVE_GIFTICON_IMAGE(false),
    NONE(false)
}
