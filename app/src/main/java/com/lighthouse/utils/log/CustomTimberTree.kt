package com.lighthouse.utils.log

import timber.log.Timber
import javax.inject.Inject

class CustomTimberTree @Inject constructor() : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return "${element.className}:${element.lineNumber}#${element.methodName}"
    }
}
