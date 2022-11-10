package com.lighthouse.data

import org.junit.Test
import java.text.SimpleDateFormat

class TextParseTest {
    private val dateFormat = SimpleDateFormat("yyyy.MM.dd")

    @Test
    fun `syrup 테스트 (1)`() {
        val text = listOf(
            "syrup gifticon",
            "다크 초콜릿 카우보이 쿠키",
            "교수령 1 개",
            "ARI L 2016.09.13",
            "사품 스티백스",
            "Syupgiticnn",
            "가프타콘 엘에는 궁파 무료콘이 발다면데-"
        )

        val parser = GifticonParser.parse(text)

        assert(parser.match)

        assert(parser.date == dateFormat.parse("2016.09.13"))

        assert(parser.title == "")

        assert("다크 초콜릿 카우보이 쿠키" in parser.candidateList)
        assert("교수령 1 개" in parser.candidateList)
        assert("사품 스티백스" in parser.candidateList)
        assert("Syupgiticnn" in parser.candidateList)
        assert("가프타콘 엘에는 궁파 무료콘이 발다면데-" in parser.candidateList)
        assert(parser.candidateList.size == 5)
    }

    @Test
    fun `syrup 테스트 (2)`() {
        val text = listOf(
            "syrup gifticon",
            "아이스 카페 라떼 Tall",
            "교환수량 1개",
            "사용기한 ~ 2016.09.11",
            "사용처 스타벅스",
            "syrup gifticon",
            "9999 6725 6650",
            "마음을 전하는 또 다른 방법... 시럽기프티콘"
        )

        val parser = GifticonParser.parse(text)

        assert(parser.match)

        assert(parser.date == dateFormat.parse("2016.09.11"))

        assert(parser.title == "")

        assert(parser.brand == "스타벅스")

        assert(parser.barcode == "999967256650")

        assert("아이스 카페 라떼 Tall" in parser.candidateList)
        assert("스타벅스" in parser.candidateList)
        assert(parser.candidateList.size == 2)
    }

    @Test
    fun `giftishow 테스트`() {
        val text = listOf(
            "내 마음의 선물, 기프티쇼 www.gitishow.com",
            "9003 6992 1874",
            ": 카페아메리카노 Tal",
            "giftishow",
            "상품명",
            "교환처 : 스타벅스",
            "유효기간 : ~ 2020.05.04"
        )

        val parser = GifticonParser.parse(text)

        assert(parser.match)

        assert(parser.date == dateFormat.parse("2020.05.04"))

        assert(parser.title == "카페아메리카노 Tal")

        assert(parser.brand == "스타벅스")

        assert(parser.barcode == "900369921874")

        assert("카페아메리카노 Tal" in parser.candidateList)
        assert("스타벅스" in parser.candidateList)
        assert(parser.candidateList.size == 2)
    }

    @Test
    fun `C Con 테스트`() {
        val text = listOf(
            "CcON 모바일 선물쿠폰 C존",
            "înumber",
            "상품명",
            "C콘",
            "9810 5989 3232",
            "스타벅스",
            "교환처",
            "유효기간 2019.06.17",
            "카페아메리카노 Tal",
            "쿠폰번호 981059893232",
            "카페아메리카노 Tal",
            "스타벅스"
        )

        val parser = GifticonParser.parse(text)

        assert(parser.match)

        assert(parser.date == dateFormat.parse("2019.06.17"))

        assert(parser.title == "")

        assert(parser.brand == "")

        assert(parser.barcode == "981059893232")

        assert("카페아메리카노 Tal" in parser.candidateList)
        assert("스타벅스" in parser.candidateList)
        assert(parser.candidateList.size == 2)
    }

    @Test
    fun `kakaotalk 테스트`() {
        val text = listOf(
            "뿌링클+콜라1.25L",
            "교환처",
            "유효기간",
            "주문번호",
            "9461 1613 3562",
            "bhe",
            "2020.10.27",
            "929975990",
            "kakaotalk선물하기"
        )

        val parser = GifticonParser.parse(text)

        assert(parser.match)

        assert(parser.date == dateFormat.parse("2020.10.27"))

        assert(parser.title == "")

        assert(parser.brand == "")

        assert(parser.barcode == "946116133562")

        assert("뿌링클+콜라1.25L" in parser.candidateList)
        assert("bhe" in parser.candidateList)
        assert(parser.candidateList.size == 2)
    }
}
