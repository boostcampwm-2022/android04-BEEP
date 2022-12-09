package com.lighthouse.presentation.ui.addgifticon

import com.lighthouse.presentation.R
import com.lighthouse.presentation.util.resource.UIText

enum class AddGifticonValid(val tag: AddGifticonTag, val text: UIText) {
    INVALID_EMPTY(
        AddGifticonTag.NONE,
        UIText.StringResource(R.string.add_gifticon_invalid_empty)
    ),
    INVALID_GIFTICON_NAME(
        AddGifticonTag.GIFTICON_NAME,
        UIText.StringResource(R.string.add_gifticon_invalid_gifticon_name)
    ),
    INVALID_BRAND_NAME(
        AddGifticonTag.BRAND_NAME,
        UIText.StringResource(R.string.add_gifticon_invalid_brand_name)
    ),
    INVALID_BRAND_CONFIRM(
        AddGifticonTag.BRAND_CONFIRM,
        UIText.StringResource(R.string.add_gifticon_invalid_brand_confirm)
    ),
    INVALID_BARCODE(
        AddGifticonTag.BARCODE,
        UIText.StringResource(R.string.add_gifticon_invalid_barcode)
    ),
    INVALID_EXPIRED_AT(
        AddGifticonTag.NONE,
        UIText.StringResource(R.string.add_gifticon_invalid_expired_at)
    ),
    INVALID_BALANCE(
        AddGifticonTag.BALANCE,
        UIText.StringResource(R.string.add_gifticon_invalid_balance)
    ),
    VALID(AddGifticonTag.NONE, UIText.Empty)
}
