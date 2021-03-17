/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 13:58
 * Description:基础的网络请求Response
 */

package com.qtimes.jectpackdemokotlin.net.base

import com.google.gson.annotations.SerializedName


class BaseResponseBody<T>(
    @SerializedName(value = "error_code", alternate = ["errorCode"]) var code: Int,
    @SerializedName(value = "reason", alternate = ["errorMsg"]) var msg: String,
    @SerializedName(value = "result", alternate = ["data"]) var data: T
)

class OptionT<T>(val value: T)