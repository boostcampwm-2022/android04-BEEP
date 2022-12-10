package com.lighthouse.util.recognizer.parser

class BarcodeParser {
    private val barcodeFilterRegex = listOf(
        "\\b(\\d{4}[- ]+\\d{4}[- ]+\\d{4}[- ]+\\d{4}[- ]+\\d{4}[- ]+\\d{4})\\b".toRegex(),
        "\\b(\\d{4}[- ]+\\d{4}[- ]+\\d{4}[- ]+\\d{4}[- ]+\\d{4}[- ]+\\d{2})\\b".toRegex(),
        "\\b(\\d{4}[- ]+\\d{4}[- ]+\\d{4}[- ]+\\d{4}[- ]+\\d{4})\\b".toRegex(),
        "\\b(\\d{4}[- ]+\\d{4}[- ]+\\d{4}[- ]+\\d{4}[- ]+\\d{2})\\b".toRegex(),
        "\\b(\\d{4}[- ]+\\d{4}[- ]+\\d{4}[- ]+\\d{4})\\b".toRegex(),
        "\\b(\\d{4}[- ]+\\d{4}[- ]+\\d{4}[- ]+\\d{2})\\b".toRegex(),
        "\\b(\\d{4}[- ]+\\d{4}[- ]+\\d{4})\\b".toRegex(),
        "\\b(\\d{16})\\b".toRegex(),
        "\\b(\\d{14})\\b".toRegex(),
        "\\b(\\d{12})\\b".toRegex()
    )

    fun parseBarcode(inputs: List<String>): BarcodeParserResult {
        var barcode = ""
        val barcodeFiltered = mutableListOf<String>()
        inputs.forEach { text ->
            val find = barcodeFilterRegex.firstNotNullOfOrNull { regex ->
                regex.find(text)
            }
            if (find == null) {
                barcodeFiltered.add(text)
            } else {
                if (barcode == "") {
                    barcode = find.groupValues.getOrNull(1)?.filter { it.isDigit() } ?: ""
                }
            }
        }
        return BarcodeParserResult(barcode, barcodeFiltered)
    }
}
