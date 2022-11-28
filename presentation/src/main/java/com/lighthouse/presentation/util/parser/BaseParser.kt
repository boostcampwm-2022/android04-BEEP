package com.lighthouse.presentation.util.parser

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class BaseParser(input: List<String>) {
    open val keywords = listOf<String>()

    open val exactExcludes = listOf("상품명", "교환처", "사용처", "유효기간", "사용기한", "쿠폰번호", "주문번호")

    open val containExcludes = listOf<String>()

    protected val info by lazy {
        input.filter {
            val value = it.lowercase().trim()
            keywords.none { keyword -> value.contains(keyword) } &&
                value !in exactExcludes &&
                containExcludes.none { exclude -> value.contains(exclude) }
        }.distinct()
    }

    open val match by lazy {
        input.isNotEmpty() && input.any { str ->
            keywords.any { keyword ->
                str.lowercase().trim().contains(keyword)
            }
        }
    }

    open val title = ""

    open val brand = ""

    private val dateRegex = "(\\d{4}.\\d{2}.\\d{2})".toRegex()
    private val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    val date by lazy {
        info.mapNotNull { str ->
            dateRegex.find(str)?.groups?.get(1)?.value?.trim()
        }.getOrNull(0)?.let {
            format.parse(it)
        } ?: Date()
    }

    private val barcodeRegex1 = "(\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4})".toRegex()
    private val barcodeRegex2 = "(\\d{4}\\s\\d{4}\\s\\d{4})".toRegex()
    val barcode by lazy {
        info.mapNotNull { str ->
            barcodeRegex1.find(str)?.groups?.get(1)?.value?.trim()?.replace(" ", "")
                ?: barcodeRegex2.find(str)?.groups?.get(1)?.value?.trim()?.replace(" ", "")
        }.getOrNull(0) ?: ""
    }

    private val cashCardRegex1 = "(\\d*)원".toRegex()
    private val cashCardRegex2 = "(\\d*)만원".toRegex()

    val isCashCard by lazy {
        balance > 0
    }

    val balance by lazy {
        info.mapNotNull { str ->
            cashCardRegex1.find(str)?.groups?.get(1)?.value?.let {
                if (it.isNotBlank()) it.trim().toInt() else 0
            } ?: cashCardRegex2.find(str)?.groups?.get(1)?.value?.let {
                if (it.isNotBlank()) it.trim().toInt() * 10000 else 0
            }
        }.getOrNull(0) ?: 0
    }

    open val candidateList by lazy {
        info.filter { str ->
            !str.all { it.isDigit() } &&
                barcodeRegex1.find(str) == null &&
                barcodeRegex2.find(str) == null &&
                dateRegex.find(str) == null
        }
    }
}
