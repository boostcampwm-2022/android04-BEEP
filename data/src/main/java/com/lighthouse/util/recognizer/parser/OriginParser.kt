package com.lighthouse.util.recognizer.parser

import com.lighthouse.util.recognizer.GifticonRecognizeInfo
import java.util.Calendar
import java.util.Date
import java.util.Locale

class OriginParser {
    private val barcodeFilterRegex = listOf(
        "(\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4})".toRegex(),
        "(\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{2})".toRegex(),
        "(\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4})".toRegex(),
        "(\\d{16})".toRegex(),
        "(\\d{14})".toRegex(),
        "(\\d{12})".toRegex()
    )

    private val dateFilterRegex = listOf(
        "(\\d{4})\\s*[년., ]\\s*(\\d{1,2})\\s*[월., ]\\s*(\\d{1,2})".toRegex()
    )

    private val contentFilterText = listOf(
        ":",
        "happy",
        "상품명",
        "수량",
        "유효기간",
        "쿠폰번호",
        "교환처",
        "사용처",
        "금액",
        "사용회",
        "사용회수",
        "사용횟수",
        "사용기한",
        "주문번호",
        "웹사이트",
        "전화주문",
        "홈페이지"
    )

    private val lineFilterText = listOf(
        "happy",
        "con",
        "coon",
        "c콘",
        "inumber",
        "înumber"
    )

    private val lineFilterRegex = listOf(
        "\\d+\\s*개".toRegex()
    )

    private val cashCardFilterRegex = listOf(
        CashCardRegex("(\\d*[,]*\\d*[,]*\\d+)원".toRegex(), 1),
        CashCardRegex("(\\d+)천원".toRegex(), 1000),
        CashCardRegex("(\\d+)만원".toRegex(), 10000),
        CashCardRegex("(\\d+)십만원".toRegex(), 100000)
    )

    fun parseBarcode(inputs: List<String>): GifticonRecognizeInfo? {
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
        return if (barcode != "") {
            GifticonRecognizeInfo(barcode = barcode, candidate = barcodeFiltered)
        } else {
            null
        }
    }

    fun parseDate(info: GifticonRecognizeInfo): GifticonRecognizeInfo {
        val dateList = mutableListOf<Date>()
        val dateFiltered = mutableListOf<String>()

        info.candidate.forEach { text ->
            val find = dateFilterRegex.firstNotNullOfOrNull { regex ->
                regex.find(text)
            }

            if (find == null) {
                dateFiltered.add(text)
            } else {
                val year = find.groupValues.getOrNull(1)?.toInt() ?: return@forEach
                val month = find.groupValues.getOrNull(2)?.toInt() ?: return@forEach
                val dayOfMonth = find.groupValues.getOrNull(3)?.toInt() ?: return@forEach
                val date = Calendar.getInstance(Locale.getDefault()).let {
                    it.set(year, month - 1, dayOfMonth)
                    it.time
                }
                dateList.add(date)
            }
        }

        return info.copy(expiredAt = dateList.max(), candidate = dateFiltered)
    }

    fun filterTrash(info: GifticonRecognizeInfo): GifticonRecognizeInfo {
        val trashFiltered = info.candidate.map { text ->
            var newText = text
            for (filter in contentFilterText) {
                newText = newText.replace(filter, "")
            }
            newText.trim()
        }.filter { text ->
            text.isNotEmpty() && !text.all { it.isDigit() } && lineFilterText.none {
                text.lowercase().contains(it)
            } && lineFilterRegex.none { regex ->
                regex.find(text) != null
            }
        }
        return info.copy(candidate = trashFiltered)
    }

    fun parseCashCard(info: GifticonRecognizeInfo): GifticonRecognizeInfo {
        val balance = info.candidate.firstNotNullOfOrNull { text ->
            cashCardFilterRegex.firstNotNullOfOrNull { cashCard ->
                val value = cashCard.regex.find(text)?.groupValues?.getOrNull(1)
                value?.filter { it.isDigit() }?.toInt()?.times(cashCard.unit)
            }
        } ?: 0
        return info.copy(isCashCard = balance > 0, balance = balance)
    }
}