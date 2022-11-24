package com.lighthouse.presentation.util.parser

class KakaoParser(input: List<String>) : BaseParser(input) {
    override val keywords = listOf("kakaotalk")

    override val exactExcludes = listOf("교환처", "유효기간", "주문번호")

    override val containExcludes = listOf<String>()
}
