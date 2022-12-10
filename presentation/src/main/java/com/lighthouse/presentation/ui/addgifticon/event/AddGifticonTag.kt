package com.lighthouse.presentation.ui.addgifticon.event

enum class AddGifticonTag(val needKeyboard: Boolean) {
    GIFTICON_NAME(true),
    BRAND_NAME(true),
    APPROVE_BRAND_NAME(false),
    BARCODE(true),
    BALANCE(true),
    APPROVE_GIFTICON_IMAGE(false),
    NONE(false)
}
