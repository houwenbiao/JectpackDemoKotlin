/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 15:29
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net.datasource

import android.annotation.SuppressLint
import com.qtimes.jectpackdemokotlin.net.HttpConfig
import com.qtimes.jectpackdemokotlin.net.RetrofitManagement
import com.qtimes.jectpackdemokotlin.net.base.BaseResponseBody
import com.qtimes.jectpackdemokotlin.net.base.BaseSubscriber
import com.qtimes.jectpackdemokotlin.net.base.RequestCallback
import com.qtimes.jectpackdemokotlin.net.base.ServerResultException
import com.qtimes.jectpackdemokotlin.net.service.ApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers


open class BaseRemoteDataSource {

    protected fun getService(): ApiService {
        return getService(ApiService::class.java, HttpConfig.BASE_URL_DEFAULT)
    }

    protected fun <T : Any> getService(clazz: Class<T>, host: String): T {
        return RetrofitManagement.instance.getService(clazz, host)
    }


    private fun <T> createData(t: T): Observable<T> {
        return Observable.create { emitter ->
            try {
                emitter.onNext(t)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    protected fun <T> execute(
        observable: Observable<BaseResponseBody<T>>,
        callback: RequestCallback<T>?
    ) {
        execute(observable, BaseSubscriber(callback))
    }


    @SuppressLint("CheckResult")
    private fun <T> execute(
        observable: Observable<BaseResponseBody<T>>,
        observer: DisposableObserver<T>,
    ) {
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(object : Function<BaseResponseBody<T>, Observable<T>> {
                override fun apply(t: BaseResponseBody<T>): Observable<T> {
                    when {
                        t.code == HttpConfig.HttpCode.CODE_SUCCESS || t.msg == "OK" -> {
                            return createData(t.data)
                        }
                        else -> {
                            throw ServerResultException(t.msg ?: "未知错误", t.code)
                        }
                    }
                }
            }).subscribeWith(observer)
    }
}