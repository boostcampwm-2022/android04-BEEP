package com.lighthouse.presentation.ui.addgifticon.adapter

import android.view.View

class AddCandidateGifticonDisplayModel(
    val item: AddGifticonItemUIModel.Gifticon,
    private val onClick: (AddGifticonItemUIModel.Gifticon) -> Unit,
    private val onDelete: (AddGifticonItemUIModel.Gifticon) -> Unit
) {

    val closeVisibility = if (item.isDelete) View.VISIBLE else View.GONE

    val badgeVisibility = if (item.isDelete.not() && item.isValid.not()) View.VISIBLE else View.GONE

    fun onClickItem() {
        if (item.isDelete) {
            onDelete(item)
        } else {
            onClick(item)
        }
    }
}
