/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 15:54
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net.datasource

import com.qtimes.jectpackdemokotlin.net.base.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking

abstract class RemoteDataSource<Api : Any>(
    iuiActionEvent: IUIActionEvent,
    apiServiceClass: Class<Api>
) : BaseRemoteDataSource<Api>(iuiActionEvent, apiServiceClass) {


    fun <Data> enqueueLoading(
        apiFun: suspend Api.() -> IHttpResponse<Data>,
        baseUrl: String = "",
        callbackFun: (RequestCallback<Data>.() -> Unit)? = null
    ): Job {
        return enqueue(
            apiFun = apiFun,
            showLoading = true,
            baseUrl = baseUrl,
            callbackFun = callbackFun
        )
    }

    /**
     * 异步请求
     */
    fun <Data> enqueue(
        apiFun: suspend Api.() -> IHttpResponse<Data>,
        showLoading: Boolean = false,
        baseUrl: String = "",
        callbackFun: (RequestCallback<Data>.() -> Unit)? = null
    ): Job {
        return launchMain {
            val callback = if (callbackFun == null) null else RequestCallback<Data>().apply {
                callbackFun.invoke(this)
            }

            try {
                if (showLoading) {
                    showLoading(coroutineContext[Job])
                }
                callback?.onStart?.invoke()
                val response: IHttpResponse<Data>
                try {
                    response = apiFun.invoke(getApiService(baseUrl))
                    if (!response.httpSuccess) {
                        throw ServerCodeBadException(response)
                    }
                } catch (throwable: Throwable) {
                    handleException(throwable, callback)
                    return@launchMain
                }
                onGetResponse(callback, response.httpData)
            } finally {
                try {
                    callback?.onFinally?.invoke()
                } finally {
                    if (showLoading) {
                        dismissLoading()
                    }
                }
            }
        }
    }

    private suspend fun <Data> onGetResponse(callback: RequestCallback<Data>?, httpData: Data) {
        callback?.let {
            withNonCancellable {
                callback.onSuccess?.let {
                    withMain {
                        it.invoke(httpData)
                    }
                }
                callback.onSuccessIO?.let {
                    withIO {
                        it.invoke(httpData)
                    }
                }
            }
        }
    }


    fun <Data> execute(
        apiFun: Api.() -> IHttpResponse<Data>,
        baseUrl: String = ""
    ): Data {
        return runBlocking {
            try {
                val asyncIO = asyncIO {
                    apiFun.invoke(getApiService(baseUrl))
                }
                val response = asyncIO.await()
                if (response.httpSuccess) {
                    return@runBlocking response.httpData
                }
                throw ServerCodeBadException(response)
            } catch (throwable: Throwable) {
                throw generateBaseExceptionReal(throwable)
            }
        }
    }
}