/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 15:13
 * Description:自定义application
 */

package com.qtimes.jetpackdemokotlin.common

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout


class MainApplication : Application() {

    init {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(
                R.color.theme_color,
                R.color.white
            ) //全局设置主题颜色
            ClassicsHeader(context)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
        context = this
        LogUtil.d("attachBaseContext")
    }

    companion object {
        lateinit var context: MainApplication
    }
}