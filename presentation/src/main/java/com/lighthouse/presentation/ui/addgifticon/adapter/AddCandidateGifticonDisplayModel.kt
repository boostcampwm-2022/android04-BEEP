package com.lighthouse.presentation.ui.addgifticon.adapter

class AddCandidateGifticonDisplayModel(
    var item: AddGifticonItemUIModel.Gifticon,
    private val onClick: (AddGifticonItemUIModel.Gifticon) -> Unit,
    private val onDelete: (AddGifticonItemUIModel.Gifticon) -> Unit
) {

    val deleteVisible
        get() = item.isDelete

    val invalidVisible
        get() = item.isDelete.not() && item.isValid.not()

    fun onClickItem() {
        if (item.isDelete) {
            onDelete(item)
        } else {
            onClick(item)
        }
    }
}
