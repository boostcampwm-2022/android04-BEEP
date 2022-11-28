package com.lighthouse.presentation.util.parser

class CConParser(input: List<String>) : BaseParser(input) {
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
