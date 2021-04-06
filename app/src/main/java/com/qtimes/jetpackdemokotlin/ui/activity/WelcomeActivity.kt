/**
 * Created with JackHou
 * Date: 2021/4/2
 * Time: 10:36
 * Description:欢迎界面Activity
 */

package com.qtimes.jetpackdemokotlin.ui.activity

import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.ui.base.BaseActivity


class WelcomeActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_welcome
    }

    override fun bindingSetViewModels() {
    }
}