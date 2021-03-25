/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 13:56
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net.service

import com.qtimes.jectpackdemokotlin.model.ForecastsBean
import com.qtimes.jectpackdemokotlin.net.HttpConfig
import com.qtimes.jectpackdemokotlin.net.base.HttpResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface ApiService {


    /**
     * 查询城市天气
     */
    @Headers(HttpConfig.HTTP_REQUEST_TYPE_KEY + ":" + HttpConfig.RequestType.WEATHER)
    @GET("weather/weatherInfo?extensions=all")
    suspend fun queryWeather(@Query("city") cityName: String): HttpResponse<List<ForecastsBean>>
}