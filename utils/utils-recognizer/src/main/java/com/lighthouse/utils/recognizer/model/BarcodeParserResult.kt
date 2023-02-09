package com.lighthouse.utils.recognizer.model

data class BarcodeParserResult(
    val barcode: String,
    val filtered: List<String>
)
