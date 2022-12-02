package com.lighthouse.domain.util

import java.util.Calendar
import java.util.Date

val today: Date
    get() = Calendar.getInstance().run {
        set(Calendar.HOUR, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        time
    }

val currentTime: Date
    get() = Calendar.getInstance().time
