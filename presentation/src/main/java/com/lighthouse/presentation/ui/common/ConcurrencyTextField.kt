package com.lighthouse.presentation.ui.common

import android.content.Context
import android.util.AttributeSet
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AbstractComposeView
import com.lighthouse.presentation.R
import com.lighthouse.presentation.ui.common.compose.ConcurrencyField

class ConcurrencyTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractComposeView(context, attrs, defStyle) {

    var value by mutableStateOf(0)
    var editable by mutableStateOf(false)
    private var listener: ValueListener? = null

    init {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.ConcurrencyTextField).run {
                value = getInt(R.styleable.ConcurrencyTextField_value, 0)
                editable = getBoolean(R.styleable.ConcurrencyTextField_editable, false)
                recycle()
            }
        }
    }

    @Composable
    override fun Content() {
        MaterialTheme {
            ConcurrencyField(value = value, editable = editable) {
                value = it
                listener?.onValueChanged(it)
            }
        }
    }

    fun addOnValueListener(listener: ValueListener) {
        this.listener = listener
    }
}

interface ValueListener {
    fun onValueChanged(value: Int)
}
