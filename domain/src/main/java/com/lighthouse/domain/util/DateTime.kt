package com.lighthouse.domain.util

import java.util.Calendar
import java.util.Date

val today: Date
    get() = Date().adjust()

val currentTime: Date
    get() = Calendar.getInstance().time

/**
 * 현재 Date 가 과거 일자인지 계산한다
 */
fun Date.isExpired(): Boolean = this@isExpired.adjust().time < today.time

/**
 * 현재 Date 의 디데이를 계산한다
 */
fun Date.calcDday(): Int = ((this@calcDday.adjust() - today).time / (1000 * 60 * 60 * 24)).toInt()

operator fun Date.minus(other: Date): Date = Date(this@minus.time - other.time)

/**
 * Date 의 시, 분, 초를 0 으로 설정한다
 */
fun Date.adjust(): Date = Calendar.getInstance().apply {
    time = this@adjust
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.time
