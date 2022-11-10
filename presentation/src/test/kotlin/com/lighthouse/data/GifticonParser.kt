package com.lighthouse.data

import java.text.SimpleDateFormat
import java.util.Date

object GifticonParser {
    fun parse(input: List<String>): Parser {
        val kakaoParser = KakaoParser(input)
        if (kakaoParser.match) {
            return kakaoParser
        }
        val syrupParser = SyrupParser(input)
        if (syrupParser.match) {
            return syrupParser
        }
        val cConParser = CConParser(input)
        if (cConParser.match) {
            return cConParser
        }
        val giftishowParser = GiftishowParser(input)
        if (giftishowParser.match) {
            return giftishowParser
        }

        return DefaultParser(input)
    }

    fun parse(input: String): Parser {
        return parse(input.split(input, "\n"))
    }
}

abstract class Parser(input: List<String>) {
    abstract val keywords: List<String>

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
        input.any { str ->
            keywords.any { keyword ->
                str.lowercase().trim().contains(keyword)
            }
        }
    }

    open val title = ""

    open val brand = ""

    private val dateRegex = "(\\d{4}.\\d{2}.\\d{2})".toRegex()
    private val format = SimpleDateFormat("yyyy.MM.dd")
    val date by lazy {
        info.mapNotNull { str ->
            dateRegex.find(str)?.groups?.get(1)?.value?.trim()
        }.getOrNull(0)?.let {
            format.parse(it)
        } ?: Date()
    }

    private val barcodeRegex = "(\\d{4}\\s\\d{4}\\s\\d{4})".toRegex()
    val barcode by lazy {
        info.mapNotNull { str ->
            barcodeRegex.find(str)?.groups?.get(1)?.value?.trim()?.replace(" ", "")
        }.getOrNull(0) ?: ""
    }

    private val cashCardRegex1 = "(\\d*)원".toRegex()
    private val cashCardRegex2 = "(\\d*)만원".toRegex()

    val isCashCard by lazy {
        balance > 0
    }

    val balance by lazy {
        info.mapNotNull { str ->
            cashCardRegex1.find(str)?.groups?.get(1)?.value?.trim()?.toInt()
                ?: cashCardRegex2.find(str)?.groups?.get(1)?.value?.trim()?.toInt()
        }.getOrNull(0) ?: 0
    }

    open val candidateList by lazy {
        info.filter { str ->
            !str.all { it.isDigit() } &&
                barcodeRegex.find(str) == null &&
                dateRegex.find(str) == null
        }
    }
}

class SyrupParser(input: List<String>) : Parser(input) {
    override val keywords = listOf("syrup")

    override val exactExcludes = listOf("사용기한", "사용처")

    override val containExcludes = listOf("교환수량", "기프티콘", "gifticon")

    private val brandRegex = "사용처\\s(.*)".toRegex()

    override val brand = info.mapNotNull {
        brandRegex.find(it)?.groups?.get(1)?.value?.trim()
    }.getOrNull(0) ?: ""

    override val candidateList = super.candidateList.map { str ->
        brandRegex.find(str)?.groups?.get(1)?.value?.trim() ?: str
    }
}

class GiftishowParser(input: List<String>) : Parser(input) {
    override val keywords = listOf("기프티쇼", "giftishow")

    override val exactExcludes = listOf("상품명", "교환처", "유효기간")

    override val containExcludes = listOf<String>()

    private val titleRegex = "상품명\\s*:(.*)".toRegex()
    private val brandRegex = "교환처\\s*:(.*)".toRegex()
    private val remainRegex = ":(.*)".toRegex()

    override val title = info.mapNotNull { str ->
        titleRegex.find(str)?.groups?.get(1)?.value?.trim()
    }.getOrNull(0) ?: info.mapNotNull { str ->
        remainRegex.find(str)?.groups?.get(1)?.value?.trim()
    }.getOrNull(0) ?: ""

    override val brand = info.mapNotNull { str ->
        brandRegex.find(str)?.groups?.get(1)?.value?.trim()
    }.getOrNull(0) ?: info.mapNotNull { str ->
        remainRegex.find(str)?.groups?.get(1)?.value?.trim()
    }.let { list ->
        list.getOrNull((list.size - 1).coerceAtLeast(1))
    } ?: ""

    override val candidateList = super.candidateList.map { str ->
        titleRegex.find(str)?.groups?.get(1)?.value?.trim() ?: brandRegex.find(str)?.groups?.get(1)?.value?.trim()
            ?: remainRegex.find(str)?.groups?.get(1)?.value?.trim() ?: str
    }
}

class CConParser(input: List<String>) : Parser(input) {
    override val keywords = listOf("ccon", "c콘")

    override val exactExcludes = listOf("înumber", "inumber", "상품명", "교환처", "유효기간", "쿠폰번호")

    override val containExcludes = listOf<String>()

    private val titleRegex = "상품명\\s+(.*)".toRegex()
    private val brandRegex = "교환처\\s+(.*)".toRegex()

    override val title = info.mapNotNull { str ->
        titleRegex.find(str)?.groups?.get(1)?.value?.trim()
    }.getOrNull(0) ?: ""

    override val brand = info.mapNotNull { str ->
        brandRegex.find(str)?.groups?.get(1)?.value?.trim()
    }.getOrNull(0) ?: ""

    override val candidateList = super.candidateList.map { str ->
        titleRegex.find(str)?.groups?.get(1)?.value?.trim() ?: brandRegex.find(str)?.groups?.get(1)?.value?.trim()
            ?: str
    }
}

class KakaoParser(input: List<String>) : Parser(input) {
    override val keywords = listOf("kakaotalk")

    override val exactExcludes = listOf("교환처", "유효기간", "주문번호")

    override val containExcludes = listOf<String>()
}

class DefaultParser(input: List<String>) : Parser(input) {
    override val keywords = listOf<String>()

    override val match = false
}
