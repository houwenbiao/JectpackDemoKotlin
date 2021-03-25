/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 14:51
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net.intercept

import com.qtimes.jectpackdemokotlin.net.HttpConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class FilterInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val builder: HttpUrl.Builder = originalRequest.url.newBuilder()
        val headers = originalRequest.headers
        if (headers != null && headers.size > 0) {
            val reqType = headers[HttpConfig.HTTP_REQUEST_TYPE_KEY]
            if (reqType != null && reqType.isNotEmpty()) {
                when (reqType) {
                    HttpConfig.RequestType.WEATHER -> builder.addQueryParameter(
                        HttpConfig.KEY,
                        HttpConfig.KEY_WEATHER
                    )
                    HttpConfig.RequestType.NEWS -> builder.addQueryParameter(
                        HttpConfig.KEY,
                        HttpConfig.KEY_NEWS
                    )
                    else -> {
                    }
                }
            }
        }
        val reqBuilder: Request.Builder = originalRequest
            .newBuilder()
            .removeHeader(HttpConfig.HTTP_REQUEST_TYPE_KEY)
            .url(builder.build())
        return chain.proceed(reqBuilder.build())
    }
}

class HeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val requestBuilder: Request.Builder = originalRequest.newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .method(originalRequest.method, originalRequest.body)
        return chain.proceed(requestBuilder.build())
    }
}