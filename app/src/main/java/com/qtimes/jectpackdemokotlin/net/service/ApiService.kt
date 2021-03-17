/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 13:56
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net.service

import com.qtimes.jectpackdemokotlin.model.Weather
import com.qtimes.jectpackdemokotlin.net.HttpConfig
import com.qtimes.jectpackdemokotlin.net.base.BaseResponseBody
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface ApiService {

    @Headers(HttpConfig.HTTP_REQUEST_TYPE_KEY + ":" + HttpConfig.RequestType.WEATHER)
    @GET("onebox/weather/query")
    fun queryWeather(@Query("cityname") cityName: String): Observable<BaseResponseBody<Weather>>
}