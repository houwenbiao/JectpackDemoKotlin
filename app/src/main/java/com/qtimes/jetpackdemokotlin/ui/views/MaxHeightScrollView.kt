/**
 * Created with JackHou
 * Date: 2021/5/7
 * Time: 11:45
 * Description:
 */

package com.qtimes.jetpackdemokotlin.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import com.qtimes.jetpackdemokotlin.R


class MaxHeightScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {
    private val maxHeight: Int

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
        )
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView)
        maxHeight = typedArray.getDimensionPixelSize(
            R.styleable.MaxHeightScrollView_max_height,
            context.resources.getDimension(R.dimen.y500).toInt()
        )
        typedArray.recycle()
    }
}