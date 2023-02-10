package com.lighthouse.presentation.ui.gifticonlist.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lighthouse.beep.model.brand.BrandWithGifticonCount
import com.lighthouse.beep.model.gifticon.Gifticon

val sampleGifticonItems = listOf<Gifticon>(
//    Gifticon(
//        id = "sample1",
//        createdAt = Date(),
//        userId = "mangbaam",
//        hasImage = false,
//        name = "별다방 아메리카노",
//        brand = "스타벅스",
//        expireAt = Date(),
//        barcode = "808346588450",
//        isCashCard = false,
//        balance = 0,
//        memo = "",
//        isUsed = false
//    ),
//    Gifticon(
//        id = "sample2",
//        createdAt = Date(),
//        userId = "mangbaam",
//        name = "5만원권",
//        hasImage = false,
//        brand = "GS25",
//        expireAt = Date(),
//        barcode = "808346588450",
//        isCashCard = true,
//        balance = 50000,
//        memo = "",
//        isUsed = false
//    ),
//    Gifticon(
//        id = "sample3",
//        createdAt = Date(),
//        userId = "mangbaam",
//        name = "어머니는 외계인",
//        brand = "베스킨라빈스",
//        expireAt = Date(),
//        hasImage = false,
//        barcode = "808346588450",
//        isCashCard = false,
//        balance = 0,
//        memo = "",
//        isUsed = true
//    ),
//    Gifticon(
//        id = "sample4",
//        createdAt = Date(),
//        userId = "mangbaam",
//        name = "3만원권",
//        brand = "e마트",
//        expireAt = Date(),
//        barcode = "808346588450",
//        isCashCard = true,
//        balance = 0,
//        hasImage = false,
//        memo = "",
//        isUsed = true
//    )
)

@Preview
@Composable
fun ChipPreview() {
    BrandChip(BrandWithGifticonCount("스타벅스", 10))
}

@Preview
@Composable
fun BrandChipsPreview() {
    BrandChipList(
        brandWithGifticonCounts = listOf(
            BrandWithGifticonCount("스타벅스", 10),
            BrandWithGifticonCount("베스킨라빈스", 12),
            BrandWithGifticonCount("맘스터치", 1),
            BrandWithGifticonCount("김밥천국", 3),
            BrandWithGifticonCount("투썸", 7)
        )
    )
}

// @Preview(widthDp = 320)
// @Composable
// fun GifticonItemPreview() {
//    GifticonItem(
//
// //        sampleGifticonItems[0]
//    )
// }
//
// @Preview
// @Composable
// fun GifticonListPreview() {
//    GifticonList(
//        sampleGifticonItems
//    )
// }

@Preview
@Composable
fun BrandChipsDialogPreview() {
    AllBrandChipsDialog(
        brandWithGifticonCounts = listOf(
            BrandWithGifticonCount(name = "스타벅스", count = 18),
            BrandWithGifticonCount(name = "베스킨라빈스", count = 18),
            BrandWithGifticonCount(name = "BHC", count = 18),
            BrandWithGifticonCount(name = "GS25", count = 18),
            BrandWithGifticonCount(name = "CU", count = 18),
            BrandWithGifticonCount(name = "서브웨이", count = 18),
            BrandWithGifticonCount(name = "세븐일레븐", count = 18),
            BrandWithGifticonCount(name = "파파존스", count = 18)
        )
    )
}
