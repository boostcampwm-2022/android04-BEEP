package com.lighthouse.util

import java.util.UUID

object UUID {
    fun generate() = UUID.randomUUID().toString()
}
