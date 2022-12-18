package com.lighthouse.presentation.ui.edit.modifygifticon.event

import com.lighthouse.presentation.R
import com.lighthouse.presentation.util.resource.UIText

enum class ModifyGifticonValid(val tag: ModifyGifticonTag, val text: UIText) {
    INVALID_NOTHING_CHANGED(
        ModifyGifticonTag.NONE,
        UIText.StringResource(R.string.modify_gifticon_invalid_nothing_changed)
    ),
    INVALID_GIFTICON_NAME(
        ModifyGifticonTag.GIFTICON_NAME,
        UIText.StringResource(R.string.edit_gifticon_invalid_gifticon_name)
    ),
    INVALID_BRAND_NAME(
        ModifyGifticonTag.BRAND_NAME,
        UIText.StringResource(R.string.edit_gifticon_invalid_brand_name)
    ),
    INVALID_APPROVE_BRAND_NAME(
        ModifyGifticonTag.APPROVE_BRAND_NAME,
        UIText.StringResource(R.string.edit_gifticon_invalid_approve_brand_name)
    ),
    INVALID_BARCODE(
        ModifyGifticonTag.BARCODE,
        UIText.StringResource(R.string.edit_gifticon_invalid_barcode)
    ),
    INVALID_EXPIRED_AT(
        ModifyGifticonTag.NONE,
        UIText.StringResource(R.string.edit_gifticon_invalid_expired_at)
    ),
    INVALID_APPROVE_EXPIRED_AT(
        ModifyGifticonTag.NONE,
        UIText.StringResource(R.string.edit_gifticon_invalid_approve_expired_at)
    ),
    INVALID_BALANCE(
        ModifyGifticonTag.BALANCE,
        UIText.StringResource(R.string.edit_gifticon_invalid_balance)
    ),
    VALID(ModifyGifticonTag.NONE, UIText.Empty)
}
