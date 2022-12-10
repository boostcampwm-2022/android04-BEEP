package com.lighthouse.util.recognizer.parser

import java.util.Calendar
import java.util.Date
import java.util.LinkedList
import java.util.Locale
import java.util.Queue

class ExpiredParser {
    private val dateFilterRegex = listOf(
        "(\\d{4})\\s*[-/년., ]\\s*(\\d{1,2})\\s*[-/월., ]\\s*(\\d{1,2})".toRegex(),
        "\\b(\\d{4})(\\d{2})(\\d{2})\\b".toRegex()
    )

    private val expiredFilterRegex = listOf(
        "만료[^\\d]*(\\d*)일".toRegex()
    )

    private fun parseDateFormat(inputs: List<String>): ExpiredParserResult {
        val dateList = mutableListOf<Date>()
        val dateFiltered = mutableListOf<String>()

        val queue: Queue<String> = LinkedList<String>().apply {
            addAll(inputs)
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
        return ExpiredParserResult(dateList.maxOrNull() ?: Date(0), dateFiltered)
    }

    private fun parseExpiredFormat(inputs: List<String>): ExpiredParserResult {
        val dateList = mutableListOf<Date>()
        val dateFiltered = mutableListOf<String>()

        inputs.forEach { text ->
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
        return ExpiredParserResult(dateList.maxOrNull() ?: Date(0), dateFiltered)
    }

    fun parseExpiredDate(inputs: List<String>): ExpiredParserResult {
        val dateFormatResult = parseDateFormat(inputs)
        val expiredFormatResult = parseExpiredFormat(dateFormatResult.filtered)
        val expiredDate = dateFormatResult.expired.coerceAtLeast(expiredFormatResult.expired)
        return ExpiredParserResult(expiredDate, expiredFormatResult.filtered)
    }
}
