package com.lighthouse.util.recognizer.recognizer.smilecon

import com.lighthouse.util.recognizer.parser.BaseParser

class SmileConParser : BaseParser() {

    override val keywordText = listOf(
        "스마일콘",
        "오피스콘",
        "smile"
    )
}
