package com.lighthouse.presentation.ui.edit.modifygifticon.event

enum class ModifyGifticonTag(val needKeyboard: Boolean) {
    GIFTICON_NAME(true),
    BRAND_NAME(true),
    APPROVE_BRAND_NAME(false),
    BARCODE(true),
    BALANCE(true),
    NONE(false)
}
