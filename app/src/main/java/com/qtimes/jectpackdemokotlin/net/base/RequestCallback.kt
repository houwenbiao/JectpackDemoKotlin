/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 14:21
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net.base

public interface RequestCallback<T> {
    fun onSuccess(t: T)
}

public interface RequestMultiplyCallback<T> : RequestCallback<T> {
    fun onFail(exception: BaseException)
}

public interface RequestMultiplyToastCallback<T> : RequestMultiplyCallback<T>