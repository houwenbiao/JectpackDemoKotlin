/**
 * Created with JackHou
 * Date: 2021/4/2
 * Time: 10:36
 * Description:欢迎界面Activity
 */

package com.qtimes.jetpackdemokotlin.ui.activity

import android.os.Bundle
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.ui.base.BaseActivity
import com.qtimes.jetpackdemokotlin.utils.ScreenUtil
import qiu.niorgai.StatusBarCompat


class WelcomeActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_welcome
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.translucentStatusBar(this, false)
        ScreenUtil.displayMetrics(this)
    }

    override fun bindingSetViewModels() {
    }
}