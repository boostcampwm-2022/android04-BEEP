package com.lighthouse.common.recognizer.parser

data class BarcodeParserResult(
    val barcode: String,
    val filtered: List<String>
)
