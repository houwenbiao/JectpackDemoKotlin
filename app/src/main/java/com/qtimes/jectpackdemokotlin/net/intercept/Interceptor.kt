/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 14:51
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net.intercept

import com.qtimes.jectpackdemokotlin.net.HttpConfig
import com.qtimes.jectpackdemokotlin.net.base.ServerResultException
import okhttp3.*
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
            .addHeader("Accept-Encoding", "gzip")
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .method(originalRequest.method, originalRequest.body)
        return chain.proceed(requestBuilder.build())
    }
}

class HttpInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val req: Request = chain.request()
        val originalRes: Response = chain.proceed(req)
        if (originalRes.code != 200) {
            throw ServerResultException(originalRes.message, originalRes.code)
        }
        val source = originalRes.body!!.source()
        source.request(Int.MAX_VALUE.toLong())
        val byteStr = source.buffer.snapshot().utf8()
        val body = ResponseBody.create(null, byteStr)
        return originalRes.newBuilder().body(body).build()
    }
}