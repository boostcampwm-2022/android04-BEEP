package com.lighthouse.presentation.ui.edit.addgifticon.event

import com.lighthouse.core.android.utils.resource.UIText
import com.lighthouse.presentation.R

enum class AddGifticonValid(val tag: AddGifticonTag, val text: UIText) {
    INVALID_EMPTY(
        AddGifticonTag.NONE,
        UIText.StringResource(R.string.edit_gifticon_invalid_empty)
    ),
    INVALID_GIFTICON_NAME(
        AddGifticonTag.GIFTICON_NAME,
        UIText.StringResource(R.string.edit_gifticon_invalid_gifticon_name)
    ),
    INVALID_BRAND_NAME(
        AddGifticonTag.BRAND_NAME,
        UIText.StringResource(R.string.edit_gifticon_invalid_brand_name)
    ),
    INVALID_APPROVE_BRAND_NAME(
        AddGifticonTag.APPROVE_BRAND_NAME,
        UIText.StringResource(R.string.edit_gifticon_invalid_approve_brand_name)
    ),
    INVALID_BARCODE(
        AddGifticonTag.BARCODE,
        UIText.StringResource(R.string.edit_gifticon_invalid_barcode)
    ),
    INVALID_EXPIRED_AT(
        AddGifticonTag.NONE,
        UIText.StringResource(R.string.edit_gifticon_invalid_expired_at)
    ),
    INVALID_APPROVE_EXPIRED_AT(
        AddGifticonTag.NONE,
        UIText.StringResource(R.string.edit_gifticon_invalid_approve_expired_at)
    ),
    INVALID_BALANCE(
        AddGifticonTag.BALANCE,
        UIText.StringResource(R.string.edit_gifticon_invalid_balance)
    ),
    INVALID_APPROVE_GIFTICON_IMAGE(
        AddGifticonTag.APPROVE_GIFTICON_IMAGE,
        UIText.StringResource(R.string.edit_gifticon_invalid_approve_gifticon_image)
    ),
    VALID(AddGifticonTag.NONE, UIText.Empty)
}
