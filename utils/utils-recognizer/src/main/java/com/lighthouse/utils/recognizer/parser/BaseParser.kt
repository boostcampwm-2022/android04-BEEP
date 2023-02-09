package com.lighthouse.utils.recognizer.parser

import com.lighthouse.utils.recognizer.model.GifticonRecognizeInfo
import com.lighthouse.utils.recognizer.processor.GifticonProcessTextTag

internal abstract class BaseParser {

    protected abstract val keywordText: List<String>

    fun match(inputs: List<String>) = keywordText.find { keyword ->
        inputs.any { it.lowercase().contains(keyword) }
    } != null

    private fun parseGifticonName(info: GifticonRecognizeInfo, inputs: List<String>): GifticonRecognizeInfo {
        val name = inputs.joinToString("")
        return info.copy(name = name, candidate = info.candidate + listOf(name))
    }

    private fun parseBrandName(info: GifticonRecognizeInfo, inputs: List<String>): GifticonRecognizeInfo {
        val brandName = inputs.joinToString("")
        return info.copy(brand = brandName, candidate = info.candidate + listOf(brandName))
    }

    private fun parseGifticonBrandName(info: GifticonRecognizeInfo, inputs: List<String>): GifticonRecognizeInfo {
        val gifticonInputs: List<String>
        val brandInputs: List<String>
        if (inputs.size > 1) {
            gifticonInputs = inputs.subList(0, inputs.lastIndex)
            brandInputs = listOf(inputs.last())
        } else {
            gifticonInputs = inputs
            brandInputs = listOf("")
        }
        val newInfo = parseGifticonName(info, gifticonInputs)
        return parseBrandName(newInfo, brandInputs)
    }

    private fun parseBrandGifticonName(info: GifticonRecognizeInfo, inputs: List<String>): GifticonRecognizeInfo {
        val gifticonInputs: List<String>
        val brandInputs: List<String>
        if (inputs.size > 1) {
            gifticonInputs = inputs.subList(1, inputs.size)
            brandInputs = listOf(inputs.first())
        } else {
            gifticonInputs = inputs
            brandInputs = listOf("")
        }
        val newInfo = parseGifticonName(info, gifticonInputs)
        return parseBrandName(newInfo, brandInputs)
    }

    fun parseText(
        info: GifticonRecognizeInfo,
        tag: GifticonProcessTextTag,
        inputs: List<String>
    ): GifticonRecognizeInfo {
        return when (tag) {
            GifticonProcessTextTag.GIFTICON_NAME -> parseGifticonName(info, inputs)
            GifticonProcessTextTag.BRAND_NAME -> parseBrandName(info, inputs)
            GifticonProcessTextTag.GIFTICON_BRAND_NAME -> parseGifticonBrandName(info, inputs)
            GifticonProcessTextTag.BRAND_GIFTICON_NAME -> parseBrandGifticonName(info, inputs)
        }
    }
}
