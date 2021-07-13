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
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.qtimes.jetpackdemokotlin.R


class BackTitleBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var tvBackTitle: TextView
    private var imgBack: ImageView
    private var txtBack: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.back_layout, this)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BackTitleBar)
        tvBackTitle = findViewById(R.id.txt_back_title)
        imgBack = findViewById(R.id.img_back)
        txtBack = findViewById(R.id.txt_back)

        tvBackTitle.text = typedArray.getText(R.styleable.BackTitleBar_title)
        tvBackTitle.setTextColor(
            typedArray.getColor(
                R.styleable.BackTitleBar_titleTextColor,
                Color.WHITE
            )
        )
        tvBackTitle.textSize =
            typedArray.getDimension(R.styleable.BackTitleBar_titleTextSize, 14f)
        typedArray.recycle()
    }

    fun onBackClickListener(onClickListener: OnClickListener) {
        imgBack.setOnClickListener(onClickListener)
        txtBack.setOnClickListener(onClickListener)
    }

    fun setTitle(title: String) {
        tvBackTitle.text = title
    }
}