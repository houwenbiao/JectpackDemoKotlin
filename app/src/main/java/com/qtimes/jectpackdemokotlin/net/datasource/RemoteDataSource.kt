/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 15:54
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net.datasource

import com.qtimes.jectpackdemokotlin.model.Weather
import com.qtimes.jectpackdemokotlin.net.base.RequestCallback


class WeatherRemoteDataSource : BaseRemoteDataSource() {

    fun queryWeather(cityName: String, callback: RequestCallback<Weather>) {
        execute(getService().queryWeather(cityName), callback)
    }
}