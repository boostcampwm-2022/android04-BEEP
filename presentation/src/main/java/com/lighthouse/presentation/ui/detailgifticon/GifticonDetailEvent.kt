package com.lighthouse.presentation.ui.detailgifticon

sealed class GifticonDetailEvent {
    object ScrollDownForUseButtonClicked : GifticonDetailEvent()
    object ShareButtonClicked : GifticonDetailEvent()
    object ShowAllUsedInfoButtonClicked : GifticonDetailEvent()
    data class ShowOriginalImage(val origin: String) : GifticonDetailEvent()
    data class ShowLargeBarcode(val barcode: String) : GifticonDetailEvent()
    object EditButtonClicked : GifticonDetailEvent()
    object ExistEmptyInfo : GifticonDetailEvent()
    object ExpireDateClicked : GifticonDetailEvent()
    object UseGifticonButtonClicked : GifticonDetailEvent()
    object UseGifticonComplete : GifticonDetailEvent()
}
