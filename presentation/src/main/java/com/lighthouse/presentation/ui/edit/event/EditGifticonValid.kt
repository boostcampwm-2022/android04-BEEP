package com.lighthouse.presentation.ui.edit.event

import com.lighthouse.presentation.R
import com.lighthouse.presentation.util.resource.UIText

enum class EditGifticonValid(val tag: EditGifticonTag, val text: UIText) {
    INVALID_EMPTY(
        EditGifticonTag.NONE,
        UIText.StringResource(R.string.edit_gifticon_invalid_empty)
    ),
    INVALID_GIFTICON_NAME(
        EditGifticonTag.GIFTICON_NAME,
        UIText.StringResource(R.string.edit_gifticon_invalid_gifticon_name)
    ),
    INVALID_BRAND_NAME(
        EditGifticonTag.BRAND_NAME,
        UIText.StringResource(R.string.edit_gifticon_invalid_brand_name)
    ),
    INVALID_APPROVE_BRAND_NAME(
        EditGifticonTag.APPROVE_BRAND_NAME,
        UIText.StringResource(R.string.edit_gifticon_invalid_approve_brand_name)
    ),
    INVALID_BARCODE(
        EditGifticonTag.BARCODE,
        UIText.StringResource(R.string.edit_gifticon_invalid_barcode)
    ),
    INVALID_EXPIRED_AT(
        EditGifticonTag.NONE,
        UIText.StringResource(R.string.edit_gifticon_invalid_expired_at)
    ),
    INVALID_APPROVE_EXPIRED_AT(
        EditGifticonTag.NONE,
        UIText.StringResource(R.string.edit_gifticon_invalid_approve_expired_at)
    ),
    INVALID_BALANCE(
        EditGifticonTag.BALANCE,
        UIText.StringResource(R.string.edit_gifticon_invalid_balance)
    ),
    INVALID_APPROVE_GIFTICON_IMAGE(
        EditGifticonTag.APPROVE_GIFTICON_IMAGE,
        UIText.StringResource(R.string.edit_gifticon_invalid_approve_gifticon_image)
    ),
    VALID(EditGifticonTag.NONE, UIText.Empty)
}
