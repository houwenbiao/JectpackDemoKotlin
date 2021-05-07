/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 13:56
 * Description:
 */

package com.qtimes.jetpackdemokotlin.net.service

import com.qtimes.jetpackdemokotlin.model.*
import com.qtimes.jetpackdemokotlin.net.HttpConfig
import com.qtimes.jetpackdemokotlin.net.base.HttpResponse
import retrofit2.http.*


interface ApiService {

    /**
     * 设备认证
     */
    @POST("device/activate")
    suspend fun authenticateDev(@Body body: AuthenticateBody): HttpResponse<AuthenticateRsp>

    /**
     * 获取门类型列表
     */
    @GET("device/doorList")
    suspend fun getDoorList(@Query("did") deviceName: String): HttpResponse<MutableList<DoorInfo>>


    /**
     * 查询省份
     */
    @Headers(HttpConfig.HTTP_REQUEST_TYPE_KEY + ":" + HttpConfig.RequestType.WEATHER)
    @GET("config/district")
    suspend fun queryProvince(): HttpResponse<List<DistrictBean>>

    /**
     * 获取城市
     */
    @Headers(HttpConfig.HTTP_REQUEST_TYPE_KEY + ":" + HttpConfig.RequestType.WEATHER)
    @GET("config/district")
    suspend fun queryCity(@Query("keywords") keywords: String): HttpResponse<List<DistrictBean>>

    /**
     * 获取县
     */
    @Headers(HttpConfig.HTTP_REQUEST_TYPE_KEY + ":" + HttpConfig.RequestType.WEATHER)
    @GET("config/district")
    suspend fun queryCounty(@Query("keywords") keywords: String): HttpResponse<List<DistrictBean>>


    /**
     * 查询城市天气
     */
    @Headers(HttpConfig.HTTP_REQUEST_TYPE_KEY + ":" + HttpConfig.RequestType.WEATHER)
    @GET("weather/weatherInfo?extensions=all")
    suspend fun queryWeather(@Query("city") cityName: String): HttpResponse<List<ForecastsBean>>


    /**
     * 获取github仓库
     */
    @Headers(HttpConfig.HTTP_REQUEST_TYPE_KEY + ":" + HttpConfig.RequestType.REPOSITORY)
    @GET("search/repositories?sort=stars&q=Android")
    suspend fun queryRepositories(@Query("page") page: Int, @Query("per_page") perPage: Int)
            : HttpResponse<List<GithubRepository>>
}