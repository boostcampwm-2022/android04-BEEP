package com.lighthouse.utils.recognizer.recognizer.smilecon

import com.lighthouse.utils.recognizer.parser.BaseParser

internal class SmileConParser : BaseParser() {

    override val keywordText = listOf(
        "스마일콘",
        "오피스콘",
        "smile"
    )
}
