/**
 * Created with JackHou
 * Date: 2021/3/23
 * Time: 14:08
 * Description:网络请求返回结果必须包含的几种参数类型
 */

package com.qtimes.jectpackdemokotlin.net.base


interface IHttpResponse<T> {

    val errorCode: Int//服务器返回的数据,标识请求是否成功

    val errorMsg: String//服务器返回的请求错误的原因

    val httpData: T//服务端返回的数据

    val httpSuccess: Boolean//http请求是否成功

    val httpFailed: Boolean
        get() = !httpSuccess
}