package com.lighthouse.core.exts

fun String.toDigit(): Int {
    return filter { it.isDigit() }.toIntOrNull() ?: 0
}
