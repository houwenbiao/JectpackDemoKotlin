/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 15:01
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net.base

import com.qtimes.jectpackdemokotlin.common.ToastHolder
import io.reactivex.rxjava3.observers.DisposableObserver


class BaseSubscriber<T> constructor(private val callback: RequestCallback<T>?) :
    DisposableObserver<T>() {

    override fun onNext(t: T) {
        callback?.onSuccess(t)
    }

    override fun onError(e: Throwable) {
        callback?.let {
            val message = e.message
            val msg: String = if (message.isNullOrBlank()) "未知错误" else message
            when (callback) {
                is RequestMultiplyToastCallback -> {
                    ToastHolder.showToast(msg = msg)
                    if (e is BaseException) {
                        callback.onFail(e)
                    } else {
                        callback.onFail(ServerResultException(errorMsg = msg))
                    }
                }
                is RequestMultiplyCallback -> {
                    if (e is BaseException) {
                        callback.onFail(e)
                    } else {
                        callback.onFail(ServerResultException(errorMsg = msg))
                    }
                }
                else -> {
                    ToastHolder.showToast(msg = msg)
                }
            }

        }
    }

    override fun onComplete() {

    }
}