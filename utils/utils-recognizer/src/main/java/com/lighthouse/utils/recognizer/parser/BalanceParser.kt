package com.lighthouse.utils.recognizer.parser

import com.lighthouse.utils.recognizer.model.BalanceParserResult

internal class BalanceParser {
    private val cashCardFilterRegex = listOf(
        CashCardRegex("\\b(\\d{0,3}[,]?\\d{0,3}[,]\\d{1,3})".toRegex(), 1),
        CashCardRegex("(\\d+)천원".toRegex(), 1000),
        CashCardRegex("(\\d+)만원".toRegex(), 10000),
        CashCardRegex("(\\d+)십만원".toRegex(), 100000)
    )

    fun parseCashCard(inputs: List<String>): BalanceParserResult {
        val balance = inputs.firstNotNullOfOrNull { text ->
            cashCardFilterRegex.firstNotNullOfOrNull { cashCard ->
                val value = cashCard.regex.find(text)?.groupValues?.getOrNull(1)
                value?.filter { it.isDigit() }?.toInt()?.times(cashCard.unit)
            }
        } ?: 0

        return BalanceParserResult(balance = if (balance >= 100) balance else 0)
    }
}
