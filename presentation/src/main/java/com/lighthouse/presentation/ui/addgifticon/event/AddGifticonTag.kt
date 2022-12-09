package com.lighthouse.presentation.ui.addgifticon.event

enum class AddGifticonTag(val needKeyboard: Boolean) {
    GIFTICON_NAME(true),
    BRAND_NAME(true),
    BRAND_CONFIRM(false),
    BARCODE(true),
    BALANCE(true),
    NONE(false)
}
