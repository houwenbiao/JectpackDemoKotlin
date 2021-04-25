/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 15:13
 * Description:
 */

package com.qtimes.jetpackdemokotlin.common

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.multidex.MultiDex
import com.qtimes.jetpackdemokotlin.utils.LogUtil


class MainApplication : Application() {
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