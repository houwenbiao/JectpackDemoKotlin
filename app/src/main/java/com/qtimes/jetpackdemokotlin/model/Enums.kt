/**
 * Created with JackHou
 * Date: 2021/4/30
 * Time: 14:52
 * Description:
 */

package com.qtimes.jetpackdemokotlin.model


enum class AtcState(val code: Int, val desc: String) {
    UNAUTHENTICATED(1, "设备未认证"),//未认证
    AUTHENTICATING(2, "设备认证中..."),//认证中
    AUTHENTICATED(3, "设备已认证"), //已认证
    AUTHENTICATE_FAILED(4, "设备认证失败");//设备认证失败
}

enum class CameraAngle(val angle: Int, val desc: String) {
    ZERO(0, "0°摄像头"),
    NINETY(90, "90°摄像头")
}