package com.lighthouse.presentation.util.resource

import android.content.Context
import androidx.annotation.StringRes

sealed class UIText {

    object Empty : UIText()

    data class DynamicString(val string: String) : UIText()

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UIText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> string
            is StringResource -> context.getString(resId, *args)
            is Empty -> ""
        }
    }
}
