/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 14:45
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net

import com.qtimes.jectpackdemokotlin.BuildConfig
import com.qtimes.jectpackdemokotlin.net.HttpConfig.Companion.CONNECT_TIMEOUT
import com.qtimes.jectpackdemokotlin.net.HttpConfig.Companion.READ_TIMEOUT
import com.qtimes.jectpackdemokotlin.net.HttpConfig.Companion.WRITE_TIMEOUT
import com.qtimes.jectpackdemokotlin.net.intercept.FilterInterceptor
import com.qtimes.jectpackdemokotlin.net.intercept.HeaderInterceptor
import com.qtimes.jectpackdemokotlin.net.intercept.HttpInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit


class RetrofitManagement private constructor() {

    companion object {
        val instance: RetrofitManagement by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitManagement()
        }
    }

    private val serviceMap = ConcurrentHashMap<String, Any>()

    private fun createRetrofit(url: String): Retrofit {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .addInterceptor(HttpInterceptor())
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(FilterInterceptor())
            .retryOnConnectionFailure(true)
        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(httpLoggingInterceptor)
        }
        val client: OkHttpClient = builder.build()
        return Retrofit.Builder()
            .client(client)
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create()) //
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    fun <T : Any> getService(clz: Class<T>, host: String): T {
        if (serviceMap.containsKey(host)) {
            val obj = serviceMap[host] as? T
            obj?.let {
                return obj
            }
        }
        val value = createRetrofit(host).create(clz)
        serviceMap[host] = value
        return value
    }
}