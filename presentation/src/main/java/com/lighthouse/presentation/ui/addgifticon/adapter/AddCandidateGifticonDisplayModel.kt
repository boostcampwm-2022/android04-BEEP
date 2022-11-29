package com.lighthouse.presentation.ui.addgifticon.adapter

import com.lighthouse.presentation.R

class AddCandidateGifticonDisplayModel(
    val item: AddGifticonItemUIModel.Gifticon,
    private val onClick: (AddGifticonItemUIModel.Gifticon) -> Unit,
    private val onDelete: (AddGifticonItemUIModel.Gifticon) -> Unit
) {

    val badgeVisibility = when {
        item.isDelete || item.isValid.not() -> true
        else -> false
    }

    val badgeResId = when {
        item.isDelete -> R.drawable.ic_item_delete
        item.isValid.not() -> R.drawable.ic_item_invalid
        else -> null
    }

    fun onClickItem() {
        if (item.isDelete) {
            onDelete(item)
        } else {
            onClick(item)
        }
    }
}
