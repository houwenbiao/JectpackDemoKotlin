/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 16:01
 * Description:
 */

package com.qtimes.jectpackdemokotlin.repository.base

import com.qtimes.jectpackdemokotlin.model.Weather
import com.qtimes.jectpackdemokotlin.net.base.RequestCallback
import com.qtimes.jectpackdemokotlin.net.datasource.WeatherRemoteDataSource


class WeatherRepository(private val dataSource: WeatherRemoteDataSource) :
    BaseRepository<WeatherRemoteDataSource>(dataSource) {

    fun queryWeather(cityName: String, callback: RequestCallback<Weather>) {
        dataSource.queryWeather(cityName, callback)
    }
}