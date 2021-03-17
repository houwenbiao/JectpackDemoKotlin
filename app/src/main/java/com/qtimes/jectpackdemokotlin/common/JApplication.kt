/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 15:13
 * Description:
 */

package com.qtimes.jectpackdemokotlin.common

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex


class JApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
        context = this
    }

    companion object {
        lateinit var context: JApplication
    }
}