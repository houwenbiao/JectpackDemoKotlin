/**
 * Created with JackHou
 * Date: 2021/5/19
 * Time: 14:00
 * Description:WebSocket相关的逻辑
 */

package com.qtimes.jetpackdemokotlin.janus

import com.qtimes.jetpackdemokotlin.utils.LogUtil
import okhttp3.*

class WebSocketChannel {

    companion object {
        const val TAG = "WebSocketChannel: "
    }

    private lateinit var webSocket: WebSocket
    private var connected = false
    private lateinit var webSocketCallback: WebSocketCallback


    /* WebSocket 子协议只是添加一个 Sec-WebSocket-Protocol 的 http 请求头，
     * 告诉服务器我们要使用 janus-protocol 这种协议来通信了。
     * Response 中也会返回这个头。
     * Sec-WebSocket-Protocol=janus-protocol"
     */
    fun connect(url: String) {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .header("Sec-WebSocket-Protocol", "janus-protocol")
            .url(url)
            .build()
        webSocket = client.newWebSocket(request, WebSocketHandler())
    }

    /**
     * 判断是否已经连接
     */
    fun isConnected(): Boolean {
        return connected
    }

    /**
     * 发送消息
     */
    fun sendMessage(msg: String) {
        if (connected) {
            LogUtil.d("WS send msg------>: $msg")
            webSocket.send(msg)
        } else {
            LogUtil.e("Send message failed socket not connected")
        }
    }

    /**
     * close WebSocket
     */
    fun close() {
        webSocket.close(1000, "manual close")
    }


    fun setWebSocketCallback(callback: WebSocketCallback) {
        this.webSocketCallback = callback
    }

    private inner class WebSocketHandler : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            LogUtil.d("onOpen")
            connected = true
            webSocketCallback.onOpen()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            webSocketCallback.onMessage(text)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            LogUtil.d("onClosed $reason")
            connected = false
            webSocketCallback.onClosed()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            LogUtil.d("onFailure response: $response")
            connected = false
            webSocketCallback.onClosed()
        }
    }


    interface WebSocketCallback {
        fun onOpen()
        fun onMessage(txt: String)
        fun onClosed()
    }
}