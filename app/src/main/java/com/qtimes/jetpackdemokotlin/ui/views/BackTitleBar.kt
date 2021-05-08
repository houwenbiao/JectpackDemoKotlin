/**
 * Created with JackHou
 * Date: 2021/5/8
 * Time: 14:18
 * Description:自定义顶部返回布局
 */

package com.qtimes.jetpackdemokotlin.ui.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.qtimes.jetpackdemokotlin.R
import kotlinx.android.synthetic.main.back_layout.view.*


class BackTitleBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    init {
        LayoutInflater.from(context).inflate(R.layout.back_layout, this)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BackTitleBar)
        txt_back_title.text = typedArray.getText(R.styleable.BackTitleBar_title)
        txt_back_title.setTextColor(
            typedArray.getColor(
                R.styleable.BackTitleBar_titleTextColor,
                Color.WHITE
            )
        )
        txt_back_title.textSize =
            typedArray.getDimension(R.styleable.BackTitleBar_titleTextSize, 18f)
        typedArray.recycle()
    }

    fun onBackClickListener(onClickListener: OnClickListener) {
        img_back.setOnClickListener(onClickListener)
        txt_back.setOnClickListener(onClickListener)
    }

    fun setTitle(title: String) {
        txt_back_title.text = title
    }
}