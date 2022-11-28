package com.lighthouse.presentation.util.parser

class DefaultParser(input: List<String>) : BaseParser(input) {
    override val keywords = listOf("gifticon")

    override val match by lazy {
        false
    }

    override val title by lazy {
        candidateList.getOrElse(0) { "" }
    }

    override val brand by lazy {
        candidateList.getOrElse(1) { "" }
    }
}
