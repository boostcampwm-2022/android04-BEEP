package com.lighthouse.presentation.ui.gifticonlist.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lighthouse.domain.model.Brand
import com.lighthouse.domain.model.Gifticon
import java.util.Date

val sampleGifticonItems = listOf(
    Gifticon(
        id = "sample1",
        userId = "mangbaam",
        hasImage = false,
        name = "별다방 아메리카노",
        brand = "스타벅스",
        expireAt = Date(),
        barcode = "808346588450",
        isCashCard = false,
        balance = 0,
        memo = "",
        isUsed = false
    ),
    Gifticon(
        id = "sample2",
        userId = "mangbaam",
        name = "5만원권",
        hasImage = false,
        brand = "GS25",
        expireAt = Date(),
        barcode = "808346588450",
        isCashCard = true,
        balance = 50000,
        memo = "",
        isUsed = false
    ),
    Gifticon(
        id = "sample3",
        userId = "mangbaam",
        name = "어머니는 외계인",
        brand = "베스킨라빈스",
        expireAt = Date(),
        hasImage = false,
        barcode = "808346588450",
        isCashCard = false,
        balance = 0,
        memo = "",
        isUsed = true
    ),
    Gifticon(
        id = "sample4",
        userId = "mangbaam",
        name = "3만원권",
        brand = "e마트",
        expireAt = Date(),
        barcode = "808346588450",
        isCashCard = true,
        balance = 0,
        hasImage = false,
        memo = "",
        isUsed = true
    )
)

@Preview
@Composable
fun ChipPreview() {
    BrandChip(Brand("스타벅스", 10))
}

@Preview
@Composable
fun BrandChipsPreview() {
    BrandChipList(
        brands = listOf(
            Brand("스타벅스", 10),
            Brand("베스킨라빈스", 12),
            Brand("맘스터치", 1),
            Brand("김밥천국", 3),
            Brand("투썸", 7),
        )
    )
}

@Preview(widthDp = 320)
@Composable
fun GifticonItemPreview() {
    GifticonItem(
        sampleGifticonItems[0]
    )
}

@Preview
@Composable
fun GifticonListPreview() {
    GifticonList(
        sampleGifticonItems
    )
}

@Preview
@Composable
fun BrandChipsDialogPreview() {
    AllBrandChipsDialog(
        brands = listOf(
            Brand(name = "스타벅스", count = 18),
            Brand(name = "베스킨라빈스", count = 18),
            Brand(name = "BHC", count = 18),
            Brand(name = "GS25", count = 18),
            Brand(name = "CU", count = 18),
            Brand(name = "서브웨이", count = 18),
            Brand(name = "세븐일레븐", count = 18),
            Brand(name = "파파존스", count = 18)
        )
    )
}
