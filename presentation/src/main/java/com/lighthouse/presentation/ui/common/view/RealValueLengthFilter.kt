package com.lighthouse.presentation.ui.common.view

import android.text.InputFilter
import android.text.Spanned
import kotlin.math.max

class RealValueLengthFilter(private val maxLength: Int) : InputFilter {
    override fun filter(
        source: CharSequence,
        srcStart: Int,
        end: Int,
        dst: Spanned,
        dstStart: Int,
        dstEnd: Int
    ): CharSequence {
        var srcStringCount = 0
        var srcCount = 0
        val dstCount = dst.substring(0, dstStart).count { it.isDigit() } +
            dst.substring(dstEnd, dst.length).count { it.isDigit() }
        while (maxLength > srcCount + dstCount) {
            if (source.length > srcStart + srcStringCount) {
                if (source[srcStart + srcStringCount].isDigit()) {
                    srcCount += 1
                }
                srcStringCount += 1
            } else {
                break
            }
        }
        return source.substring(srcStart, srcStart + max(srcStringCount, 0))
    }
}
