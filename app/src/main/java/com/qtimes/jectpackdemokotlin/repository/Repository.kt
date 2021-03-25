/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 16:01
 * Description:
 */

package com.qtimes.jectpackdemokotlin.repository

import com.qtimes.jectpackdemokotlin.model.ForecastsBean
import com.qtimes.jectpackdemokotlin.net.base.IUIActionEvent
import com.qtimes.jectpackdemokotlin.net.base.RequestCallback
import com.qtimes.jectpackdemokotlin.repository.base.BaseRepository
import com.qtimes.jectpackdemokotlin.utils.LogUtil


class WeatherRepository(iuiActionEvent: IUIActionEvent) : BaseRepository(iuiActionEvent) {

    fun queryWeather(
        cityName: String,
        callbackFun: (RequestCallback<List<ForecastsBean>>.() -> Unit)? = null
    ) {
        remoteDataSource.enqueueLoading({ queryWeather(cityName) }, callbackFun = callbackFun)
    }
}


