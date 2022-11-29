package com.lighthouse.presentation.ui.addgifticon

import com.lighthouse.presentation.R
import com.lighthouse.presentation.util.resource.UIText

enum class AddGifticonValid(val focus: AddGifticonFocus, val text: UIText) {
    INVALID_EMPTY(
        AddGifticonFocus.NONE,
        UIText.StringResource(R.string.add_gifticon_invalid_empty)
    ),
    INVALID_GIFTICON_NAME(
        AddGifticonFocus.GIFTICON_NAME,
        UIText.StringResource(R.string.add_gifticon_invalid_gifticon_name)
    ),
    INVALID_BRAND_NAME(
        AddGifticonFocus.BRAND_NAME,
        UIText.StringResource(R.string.add_gifticon_invalid_brand_name)
    ),
    INVALID_BARCODE(
        AddGifticonFocus.BARCODE,
        UIText.StringResource(R.string.add_gifticon_invalid_barcode)
    ),
    INVALID_EXPIRED_AT(
        AddGifticonFocus.EXPIRED_AT,
        UIText.StringResource(R.string.add_gifticon_invalid_expired_at)
    ),
    INVALID_BALANCE(
        AddGifticonFocus.BALANCE,
        UIText.StringResource(R.string.add_gifticon_invalid_balance)
    ),
    VALID(AddGifticonFocus.NONE, UIText.Empty)
}
