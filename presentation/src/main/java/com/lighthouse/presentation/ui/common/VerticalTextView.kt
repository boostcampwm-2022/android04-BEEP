package com.lighthouse.presentation.ui.common

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.lighthouse.presentation.R

/**
 * TextView 를 세로로 회전한 뷰
 *
 * @property topDown true 일 때 시계 방향 회전, false 일 때 반시계 방향 회전
 *
 * 코드 참고: [stackoverflow.com/a/45414489](https://stackoverflow.com/a/45414489)
 */
class VerticalTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    var topDown: Boolean = DEFAULT_TOP_DOWN

    init {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.VerticalTextView).run {
                topDown = getBoolean(R.styleable.VerticalTextView_topDown, DEFAULT_TOP_DOWN)
                recycle()
            }
        }
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        if (topDown) {
            canvas.translate(width.toFloat(), 0f)
            canvas.rotate(90f)
        } else {
            canvas.translate(0f, height.toFloat())
            canvas.rotate(-90f)
        }
        canvas.translate(
            compoundPaddingLeft.toFloat(),
            extendedPaddingTop.toFloat()
        )
        layout.draw(canvas)
    }

    companion object {
        private const val DEFAULT_TOP_DOWN = true
    }
}
