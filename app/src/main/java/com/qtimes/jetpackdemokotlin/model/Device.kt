/**
 * Created with JackHou
 * Date: 2021/4/26
 * Time: 16:07
 * Description:设备相关实体类
 */

package com.qtimes.jetpackdemokotlin.model

import com.google.gson.annotations.SerializedName

/**
 * 激活请求的body
 */
data class AuthenticateBody(
    val productKey: String,
    val deviceName: String,
    val deviceSecret: String,
    val pushId: String,
    val androidId: String,
    val userAgent: String
)

/**
 * 激活请求的response
 */
data class AuthenticateRsp(@SerializedName(value = "access") val accessBean: AccessBean)

/**
 * 认证返回的token以及有效期
 */
data class AccessBean(val token: String, val expireIn: Int)