/**
 * Created with JackHou
 * Date: 2021/3/23
 * Time: 18:10
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net.base

import com.google.gson.annotations.SerializedName
import com.qtimes.jectpackdemokotlin.net.HttpConfig


class HttpResponse<T>(
    @SerializedName(
        value = "status",
        alternate = ["error_code"]
    ) var code: Int = HttpConfig.HttpCode.CODE_UNKNOWN,
    @SerializedName(value = "info", alternate = ["reason"]) var msg: String? = null,
    @SerializedName(value = "forecasts", alternate = ["districts", "result"]) var data: T,
) : IHttpResponse<T> {

    override val errorCode: Int
        get() = code
    override val errorMsg: String
        get() = msg ?: ""
    override val httpData: T
        get() = data
    override val httpSuccess: Boolean
        get() = code == HttpConfig.HttpCode.CODE_SUCCESS || "OK" == msg

    override fun toString(): String {
        return "HttpResBean(code=$code, message=$msg, data=$data)"
    }
}