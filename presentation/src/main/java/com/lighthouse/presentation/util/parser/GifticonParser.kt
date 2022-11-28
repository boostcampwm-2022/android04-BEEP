package com.lighthouse.presentation.util.parser

object GifticonParser {
    fun parse(input: List<String>): BaseParser {
        val kakaoParser = KakaoParser(input)
        if (kakaoParser.match) {
            return kakaoParser
        }
        val syrupParser = SyrupParser(input)
        if (syrupParser.match) {
            return syrupParser
        }
        val cConParser = CConParser(input)
        if (cConParser.match) {
            return cConParser
        }
        val giftishowParser = GiftishowParser(input)
        if (giftishowParser.match) {
            return giftishowParser
        }
        return DefaultParser(input)
    }

    fun parse(input: String): BaseParser {
        return parse(input.split(input, "\n"))
    }
}
