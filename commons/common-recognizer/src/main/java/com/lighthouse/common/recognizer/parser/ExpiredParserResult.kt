package com.lighthouse.common.recognizer.parser

import java.util.Date

data class ExpiredParserResult(
    val expired: Date,
    val filtered: List<String>
)
