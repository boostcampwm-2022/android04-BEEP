package com.lighthouse.util.recognizer.parser

import java.util.Date

data class ExpiredParserResult(
    val expired: Date,
    val filtered: List<String>
)
