package com.lighthouse.util.recognizer.parser

data class BarcodeParserResult(
    val barcode: String,
    val filtered: List<String>
)
