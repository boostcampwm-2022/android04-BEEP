package com.lighthouse.presentation.util.parser

class GiftishowParser(input: List<String>) : BaseParser(input) {
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
