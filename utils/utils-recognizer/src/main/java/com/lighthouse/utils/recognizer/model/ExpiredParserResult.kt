package com.lighthouse.utils.recognizer.model

import java.util.Date

data class ExpiredParserResult(
    val expired: Date,
    val filtered: List<String>
)
