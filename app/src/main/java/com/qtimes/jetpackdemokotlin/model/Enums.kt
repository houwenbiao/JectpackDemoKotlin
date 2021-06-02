/**
 * Created with JackHou
 * Date: 2021/4/30
 * Time: 14:52
 * Description:
 */

package com.qtimes.jetpackdemokotlin.model

import android.webkit.JsPromptResult


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

enum class JanusMessageType {
    MESSAGE,
    TRICKLE,//收集候选者用的消息。里边存放着 candidate，janus.js收到该消息后，需要将Candidate解析出来
    DESTROY,
    KEEPALIVE,//心跳消息
    CREATE,
    JOINED,
    ATTACH,
    ATTACHED,
    EVENT,//插件发布的事件消息
    ERROR,
    ACK,//确认消息, 也就是说之前客户端发送了一个信令给服务端，服务端收到之后给客户端回了一个ack，确认服务端已经收到该消息了
    SUCCESS,//消息处理成功。该消息与 ack 消息是类似的，当服务器完成了客户端的命令后会返回该消息
    WEBRTCUP,//表示一个peer上线了，此时要找到以应的业务插件（plugin）做业务处理
    HANGUP,//用户挂断，找到对应的plugin，进行挂断操作。
    DETACH,//某个插件要求与Janus Core之间断开连接
    DETACHED,
    SLOWLINK,//限流
    MEDIA,//开始或停止媒体流
    TIMEOUT;

    override fun toString(): String {
        return name
    }

    companion object {
        @ExperimentalStdlibApi
        fun fromString(string: String): JanusMessageType {
            return valueOf(string.uppercase())
        }
    }
}