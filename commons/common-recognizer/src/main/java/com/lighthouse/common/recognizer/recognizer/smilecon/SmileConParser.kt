package com.lighthouse.common.recognizer.recognizer.smilecon

import com.lighthouse.common.recognizer.parser.BaseParser

class SmileConParser : BaseParser() {

    override val keywordText = listOf(
        "스마일콘",
        "오피스콘",
        "smile"
    )
}
