/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 14:21
 * Description:
 */

package com.qtimes.jetpackdemokotlin.net.base

class RequestCallback<T>(
    internal var onSuccess: ((T) -> Unit)? = null,
    internal var onSuccessIO: (suspend (T) -> Unit)? = null
) : BaseRequestCallback() {
    /**
     * 当网络请求成功时会调用此方法，随后会先后调用 onSuccessIO、onFinally 方法
     */
    fun onSuccess(block: (data: T) -> Unit) {
        this.onSuccess = block
    }

    /**
     * 在 onSuccess 方法之后，onFinally 方法之前执行
     * 考虑到网络请求成功后有需要将数据保存到数据库之类的耗时需求，所以提供了此方法用于在 IO 线程进行执行
     * 注意外部不要在此处另开子线程，此方法会等到耗时任务完成后再执行 onFinally 方法
     */
    fun onSuccessIO(block: suspend (T) -> Unit) {
        this.onSuccessIO = block
    }
}

class RequestPairCallback<T1, T2>(
    internal var onSuccess: ((data1: T1, data2: T2) -> Unit)? = null,
    internal var onSuccessIO: (suspend (data1: T1, data2: T2) -> Unit)? = null,
) : BaseRequestCallback() {
    /**
     * 当网络请求成功时会调用此方法，随后会先后调用 onSuccessIO、onFinally 方法
     */
    fun onSuccess(block: (data1: T1, data2: T2) -> Unit) {
        this.onSuccess = block
    }

    /**
     * 在 onSuccess 方法之后，onFinally 方法之前执行
     * 考虑到网络请求成功后有需要将数据保存到数据库之类的耗时需求，所以提供了此方法用于在 IO 线程进行执行
     * 注意外部不要在此处另开子线程，此方法会等到耗时任务完成后再执行 onFinally 方法
     */
    fun onSuccessIO(block: suspend (data1: T1, data2: T2) -> Unit) {
        this.onSuccessIO = block
    }
}


class RequestTripleCallback<T1, T2, T3>(
    internal var onSuccess: ((dataA: T1, dataB: T2, dataC: T3) -> Unit)? = null,
    internal var onSuccessIO: (suspend (dataA: T1, dataB: T2, dataC: T3) -> Unit)? = null
) : BaseRequestCallback() {

    /**
     * 当网络请求成功时会调用此方法，随后会先后调用 onSuccessIO、onFinally 方法
     */
    fun onSuccess(block: (dataA: T1, dataB: T2, dataC: T3) -> Unit) {
        this.onSuccess = block
    }

    /**
     * 在 onSuccess 方法之后，onFinally 方法之前执行
     * 考虑到网络请求成功后有需要将数据保存到数据库之类的耗时需求，所以提供了此方法用于在 IO 线程进行执行
     * 注意外部不要在此处另开子线程，此方法会等到耗时任务完成后再执行 onFinally 方法
     */
    fun onSuccessIO(block: suspend (dataA: T1, dataB: T2, dataC: T3) -> Unit) {
        this.onSuccessIO = block
    }

}