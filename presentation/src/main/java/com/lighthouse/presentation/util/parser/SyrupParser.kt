package com.lighthouse.presentation.util.parser

class SyrupParser(input: List<String>) : BaseParser(input) {
    override val keywords = listOf("gifticon", "syrup")

    override val exactExcludes = listOf("사용기한", "사용처")

    override val containExcludes = listOf("교환수량", "기프티콘")

    override val match by lazy {
        input.any { str ->
            str.lowercase().trim().contains("syrup")
        } || input.count { str -> str.lowercase().trim().contains("gifticon") } == 2
    }

    private val brandRegex = "사용처\\s(.*)".toRegex()

    override val brand = info.mapNotNull {
        brandRegex.find(it)?.groups?.get(1)?.value?.trim()
    }.getOrNull(0) ?: ""

    override val candidateList = super.candidateList.map { str ->
        brandRegex.find(str)?.groups?.get(1)?.value?.trim() ?: str
    }
}
