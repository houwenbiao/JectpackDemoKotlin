/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 14:13
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net.base

import com.qtimes.jectpackdemokotlin.net.HttpConfig
import java.lang.Exception
import java.lang.RuntimeException

open class BaseHttpException(
    val errorCode: Int,
    val errorMessage: String,
    val realException: Throwable?
) : Exception(errorMessage) {
    companion object {

        //此变量用于表示在网络请求过程过程中抛出了异常
        const val CODE_ERROR_LOCAL_UNKNOWN = -1024520
    }

    val isLocalBadException: Boolean
        get() = this is LocalBadException

    val isServerCodeBadException: Boolean
        get() = this is ServerCodeBadException
}


/**
 * API 请求成功了，但 code != successCode
 */
class ServerCodeBadException(errorCode: Int, errorMsg: String) :
    BaseHttpException(errorCode, errorMsg, null) {
    constructor(response: IHttpResponse<*>) : this(response.errorCode, response.errorMsg)
}

/**
 * 请求过程抛出异常
 * @param throwable
 */
class LocalBadException(throwable: Throwable) :
    BaseHttpException(CODE_ERROR_LOCAL_UNKNOWN, throwable.message ?: "", throwable)