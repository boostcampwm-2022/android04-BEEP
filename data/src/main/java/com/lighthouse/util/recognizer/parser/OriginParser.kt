package com.lighthouse.util.recognizer.parser

import com.lighthouse.util.recognizer.GifticonRecognizeInfo
import java.util.Calendar
import java.util.Date
import java.util.LinkedList
import java.util.Locale
import java.util.Queue

class OriginParser {
    private val barcodeFilterRegex = listOf(
        "\\b(\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4})\\b".toRegex(),
        "\\b(\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{2})\\b".toRegex(),
        "\\b(\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4})\\b".toRegex(),
        "\\b(\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{2})\\b".toRegex(),
        "\\b(\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4})\\b".toRegex(),
        "\\b(\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{2})\\b".toRegex(),
        "\\b(\\d{4}\\s*[- ]\\s*\\d{4}\\s*[- ]\\s*\\d{4})\\b".toRegex(),
        "\\b(\\d{16})\\b".toRegex(),
        "\\b(\\d{14})\\b".toRegex(),
        "\\b(\\d{12})\\b".toRegex()
    )

    private val dateFilterRegex = listOf(
        "(\\d{4})\\s*[-/년., ]\\s*(\\d{1,2})\\s*[-/월., ]\\s*(\\d{1,2})".toRegex(),
        "\\b(\\d{4})(\\d{2})(\\d{2})\\b".toRegex()
    )

    private val expiredFilterRegex = listOf(
        "만료[^\\d]*(\\d*)일".toRegex()
    )

    private val cashCardFilterRegex = listOf(
        CashCardRegex("(\\d{0,3}[,]?\\d{0,3}[,]\\d{1,3})".toRegex(), 1),
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

    fun parseDateFormat(info: GifticonRecognizeInfo): GifticonRecognizeInfo {
        val dateList = mutableListOf<Date>()
        val dateFiltered = mutableListOf<String>()

        val queue: Queue<String> = LinkedList<String>().apply {
            addAll(info.candidate)
        }

        while (queue.isNotEmpty()) {
            val text = queue.poll() ?: ""
            val find = dateFilterRegex.firstNotNullOfOrNull { regex ->
                regex.find(text)
            }

            if (find == null) {
                dateFiltered.add(text)
            } else {
                val year = find.groupValues.getOrNull(1)?.toInt() ?: continue
                val month = find.groupValues.getOrNull(2)?.toInt() ?: continue
                val dayOfMonth = find.groupValues.getOrNull(3)?.toInt() ?: continue
                val date = Calendar.getInstance(Locale.getDefault()).let {
                    it.set(year, month - 1, dayOfMonth)
                    it.time
                }
                dateList.add(date)
                val end = find.groupValues.getOrNull(0)?.length
                if (end != null && end < text.length) {
                    queue.add(text.substring(end, text.length))
                }
            }
        }

        val expiredAt = dateList.maxOrNull() ?: Date(0)
        return info.copy(expiredAt = expiredAt, candidate = dateFiltered)
    }

    fun parseExpiredFormat(info: GifticonRecognizeInfo): GifticonRecognizeInfo {
        val dateList = mutableListOf<Date>()
        val dateFiltered = mutableListOf<String>()

        info.candidate.forEach { text ->
            val find = expiredFilterRegex.firstNotNullOfOrNull { regex ->
                regex.find(text)
            }

            if (find == null) {
                dateFiltered.add(text)
            } else {
                val expiredDate = find.groupValues.getOrNull(1)?.toInt() ?: return@forEach
                val date = Calendar.getInstance(Locale.getDefault()).let {
                    it.add(Calendar.DAY_OF_MONTH, expiredDate)
                    it.time
                }
                dateList.add(date)
            }
        }

        val expiredAt = dateList.maxOrNull() ?: Date(0)
        return info.copy(expiredAt = info.expiredAt.coerceAtLeast(expiredAt), candidate = dateFiltered)
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
