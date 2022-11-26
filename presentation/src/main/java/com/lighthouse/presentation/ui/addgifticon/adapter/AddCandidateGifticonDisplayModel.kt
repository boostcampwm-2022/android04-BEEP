package com.lighthouse.presentation.ui.addgifticon.adapter

import android.view.View
import com.lighthouse.presentation.model.AddGifticonUIModel

class AddCandidateGifticonDisplayModel(
    val item: AddGifticonUIModel.Gifticon,
    private val onClick: (AddGifticonUIModel.Gifticon) -> Unit,
    private val onDelete: (AddGifticonUIModel.Gifticon) -> Unit
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
