/**
 * Created with JackHou
 * Date: 2021/3/23
 * Time: 18:31
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net.datasource

import android.widget.Toast
import com.qtimes.jectpackdemokotlin.common.MainApplication
import com.qtimes.jectpackdemokotlin.net.HttpConfig
import com.qtimes.jectpackdemokotlin.net.base.IUIActionEvent
import com.qtimes.jectpackdemokotlin.net.service.ApiService


class WeatherRemoteDataSource(iuiActionEvent: IUIActionEvent) :
    RemoteExtendDataSource<ApiService>(iuiActionEvent, ApiService::class.java) {

    override val baseUrl: String
        get() = HttpConfig.BASE_URL_WEATHER

    override fun showToast(msg: String) {
        Toast.makeText(MainApplication.context, msg, Toast.LENGTH_LONG).show()
    }
}