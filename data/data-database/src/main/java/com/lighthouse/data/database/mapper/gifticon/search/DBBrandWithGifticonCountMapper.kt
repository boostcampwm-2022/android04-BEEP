package com.lighthouse.data.database.mapper.gifticon.search

import com.lighthouse.beep.model.brand.BrandWithGifticonCount
import com.lighthouse.data.database.model.DBBrandWithGifticonCount

internal fun List<DBBrandWithGifticonCount>.toDomain(): List<BrandWithGifticonCount> {
    return map {
        it.toDomain()
    }
}

internal fun DBBrandWithGifticonCount.toDomain(): BrandWithGifticonCount {
    return BrandWithGifticonCount(name, count)
}
