package com.lighthouse.domain.usecase

import com.lighthouse.domain.model.GifticonSaveRequest

class GetGifticonsByLocationUseCase {

    operator fun invoke(brandName: String, x: Int, y: Int): List<GifticonSaveRequest> {
        return emptyList()
    }
}
