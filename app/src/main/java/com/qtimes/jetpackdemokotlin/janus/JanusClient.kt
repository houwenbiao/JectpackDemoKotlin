/**
 * Created with JackHou
 * Date: 2021/5/19
 * Time: 14:28
 * Description:JanusClient
 */

package com.qtimes.jetpackdemokotlin.janus

import com.qtimes.jetpackdemokotlin.model.JanusMessageType
import com.qtimes.jetpackdemokotlin.model.PluginHandle
import com.qtimes.jetpackdemokotlin.model.Transaction
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.math.BigInteger
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap

/**
 * Author: JackHou
 * Date: 2021/5/19.
 * 参考1：https://blog.csdn.net/Java_lilin/article/details/104007291
 * 参考2：https://github.com/benwtrent/janus-gateway-android
 * 参考3：https://zhuanlan.zhihu.com/p/149324861?utm_source=wechat_session
 */
class JanusClient(private val url: String) : WebSocketChannel.WebSocketCallback {

    private val attachedPlugins: ConcurrentHashMap<BigInteger, PluginHandle> =
        ConcurrentHashMap<BigInteger, PluginHandle>()
    private val transactions: ConcurrentHashMap<String, Transaction> =
        ConcurrentHashMap<String, Transaction>()
    private var webSocketChannel: WebSocketChannel = WebSocketChannel()
    private var janusCallback: JanusCallback? = null
    private var sessionId: BigInteger? = null
    private var keepAliveJob: Job? = null

    @Volatile
    private var isKeepAliveRunning = false

    init {
        webSocketChannel.setWebSocketCallback(this)
    }

    /**
     * 设置Janus的回调
     */
    fun setJanusCallback(callback: JanusCallback) {
        janusCallback = callback
    }

    /**
     * 创建连接
     */
    fun connect() {
        webSocketChannel.connect(url)
    }

    /**
     * 主动断开连接
     */
    fun disconnect() {
        stopKeepAliveTimer()
        webSocketChannel.close()
    }

    /**
     * 创建session
     */
    private fun createSession() {
        val tid = randomString(12)
        LogUtil.d("createSession tid = $tid, transactions = $transactions")
        transactions[tid] = object : Transaction(tid) {
            @Throws(Exception::class)
            override fun onSuccess(data: JSONObject) {
                LogUtil.d("createSession onSuccess = $data")
                val param = data.getJSONObject("data")
                sessionId = BigInteger(param.getString("id"))
                startKeepAliveTimer()
                LogUtil.d("createSession: callback = $janusCallback")
                janusCallback?.onCreateSession(sessionId)
            }
        }
        try {
            val obj = JSONObject()
            obj.put("janus", "create")
            obj.put("transaction", tid)
            webSocketChannel.sendMessage(obj.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun destroySession() {
        val tid = randomString(12)
        transactions[tid] = object : Transaction(tid) {
            @Throws(Exception::class)
            override fun onSuccess(data: JSONObject) {
                stopKeepAliveTimer()
                if (janusCallback != null) {
                    janusCallback!!.onDestroySession(sessionId)
                }
            }
        }
        try {
            val obj = JSONObject()
            obj.put("janus", "destroy")
            obj.put("transaction", tid)
            obj.put("session_id", sessionId)
            webSocketChannel.sendMessage(obj.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun attachPlugin(pluginName: String?) {
        val tid = randomString(12)
        transactions[tid] = object : Transaction(tid) {
            @Throws(Exception::class)
            override fun onSuccess(data: JSONObject) {
                val param = data.getJSONObject("data")
                val handleId = BigInteger(param.getString("id"))
                if (janusCallback != null) {
                    janusCallback!!.onJanusAttached(handleId)
                }
                val handle = PluginHandle(handleId)
                attachedPlugins[handleId] = handle
            }
        }
        try {
            val obj = JSONObject()
            obj.put("janus", "attach")
            obj.put("transaction", tid)
            obj.put("plugin", pluginName)
            obj.put("session_id", sessionId)
            webSocketChannel.sendMessage(obj.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * attach 到发布者的 handler 上，准备接收视频流
     * 每个发布者都要 attach 一遍，然后协商 sdp, SFU
     *
     * @param feedId
     */
    fun subscribeAttach(feedId: BigInteger?) {
        val tid = randomString(12)
        transactions[tid] = object : Transaction(tid, feedId!!) {
            @Throws(Exception::class)
            override fun onSuccess(data: JSONObject, feedId: BigInteger) {
                val param = data.getJSONObject("data")
                val handleId = BigInteger(param.getString("id"))
                if (janusCallback != null) {
                    janusCallback!!.onSubscribeAttached(handleId, feedId)
                }
                val handle = PluginHandle(handleId)
                attachedPlugins[handleId] = handle
            }
        }
        try {
            val obj = JSONObject()
            obj.put("janus", "attach")
            obj.put("transaction", tid)
            obj.put("plugin", "janus.plugin.videoroom")
            obj.put("session_id", sessionId)
            webSocketChannel.sendMessage(obj.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createOffer(handleId: BigInteger, sdp: SessionDescription) {
        val message = JSONObject()
        try {
            val publish = JSONObject()
            publish.putOpt("audio", true)
            publish.putOpt("video", true)
            val jsep = JSONObject()
            jsep.putOpt("type", sdp.type)
            jsep.putOpt("sdp", sdp.description)
            message.putOpt("janus", "message")
            message.putOpt("body", publish)
            message.putOpt("jsep", jsep)
            message.putOpt("transaction", randomString(12))
            message.putOpt("session_id", sessionId)
            message.putOpt("handle_id", handleId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        webSocketChannel.sendMessage(message.toString())
    }

    /**
     * 开始订阅
     *
     * @param subscriptionHandleId
     * @param sdp
     */
    fun subscriptionStart(
        subscriptionHandleId: BigInteger?,
        roomId: Int,
        sdp: SessionDescription?
    ) {
        val message = JSONObject()
        try {
            val body = JSONObject()
            body.putOpt("request", "start")
            body.putOpt("room", roomId)
            message.putOpt("janus", "message")
            message.putOpt("body", body)
            message.putOpt("transaction", randomString(12))
            message.putOpt("session_id", sessionId)
            message.putOpt("handle_id", subscriptionHandleId)
            if (sdp != null) {
                val jsep = JSONObject()
                jsep.putOpt("type", sdp.type)
                jsep.putOpt("sdp", sdp.description)
                message.putOpt("jsep", jsep)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        webSocketChannel.sendMessage(message.toString())
    }

    fun publish(handleId: BigInteger?, sdp: SessionDescription) {
        val message = JSONObject()
        try {
            val publish = JSONObject()
            publish.putOpt("request", "publish")
            publish.putOpt("audio", true)
            publish.putOpt("video", true)
            val jsep = JSONObject()
            jsep.putOpt("type", sdp.type)
            jsep.putOpt("sdp", sdp.description)
            message.putOpt("janus", "message")
            message.putOpt("body", publish)
            message.putOpt("jsep", jsep)
            message.putOpt("transaction", randomString(12))
            message.putOpt("session_id", sessionId)
            message.putOpt("handle_id", handleId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        webSocketChannel.sendMessage(message.toString())
    }

    /**
     * 订阅
     *
     * @param roomId 房间ID
     * @param feedId 要订阅的ID
     */
    fun subscribe(subscriptionHandleId: BigInteger?, roomId: Int, feedId: BigInteger?) {
        val message = JSONObject()
        val body = JSONObject()
        try {
            body.putOpt("ptype", "subscriber")
            body.putOpt("request", "join")
            body.putOpt("room", roomId)
            body.putOpt("feed", feedId)
            message.put("body", body)
            message.putOpt("janus", "message")
            message.putOpt("transaction", randomString(12))
            message.putOpt("session_id", sessionId)
            message.putOpt("handle_id", subscriptionHandleId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        webSocketChannel.sendMessage(message.toString())
    }

    fun trickleCandidate(handleId: BigInteger?, iceCandidate: IceCandidate) {
        val candidate = JSONObject()
        val message = JSONObject()
        try {
            candidate.putOpt("candidate", iceCandidate.sdp)
            candidate.putOpt("sdpMid", iceCandidate.sdpMid)
            candidate.putOpt("sdpMLineIndex", iceCandidate.sdpMLineIndex)
            message.putOpt("janus", "trickle")
            message.putOpt("candidate", candidate)
            message.putOpt("transaction", randomString(12))
            message.putOpt("session_id", sessionId)
            message.putOpt("handle_id", handleId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        webSocketChannel.sendMessage(message.toString())
    }

    fun trickleCandidateComplete(handleId: BigInteger?) {
        val candidate = JSONObject()
        val message = JSONObject()
        try {
            candidate.putOpt("completed", true)
            message.putOpt("janus", "trickle")
            message.putOpt("candidate", candidate)
            message.putOpt("transaction", randomString(12))
            message.putOpt("session_id", sessionId)
            message.putOpt("handle_id", handleId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        webSocketChannel.sendMessage(message.toString())
    }

    fun joinRoom(handleId: BigInteger?, roomId: Int, displayName: String?) {
        val message = JSONObject()
        val body = JSONObject()
        try {
            body.putOpt("display", displayName)
            body.putOpt("ptype", "publisher")
            body.putOpt("request", "join")
            body.putOpt("room", roomId)
            message.put("body", body)
            message.putOpt("janus", "message")
            message.putOpt("transaction", randomString(12))
            message.putOpt("session_id", sessionId)
            message.putOpt("handle_id", handleId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        webSocketChannel.sendMessage(message.toString())
    }


    /**
     * 连接打开
     */
    override fun onOpen() {
        LogUtil.d("janus client onOpen")
        createSession()
    }

    /**
     * 接收到消息
     */
    @ExperimentalStdlibApi
    override fun onMessage(message: String) {
        LogUtil.d("Received message >>>>>>>>>> $message")
        try {
            val obj = JSONObject(message)
            val type: JanusMessageType = JanusMessageType.fromString(obj.getString("janus"))
            var transaction: String? = null
            var sender: BigInteger = BigInteger("0")
            if (obj.has("transaction")) {
                transaction = obj.getString("transaction")
            }
            if (obj.has("sender")) {
                sender = BigInteger(obj.getString("sender"))
            }
            var handle: PluginHandle? = null
            if (sender != null) {
                handle = attachedPlugins[sender]
            }
            LogUtil.d("Received message type=$type transaction = $transaction, sender = $sender")
            when (type) {
                JanusMessageType.KEEPALIVE -> {
                }
                JanusMessageType.ACK -> {
                }
                JanusMessageType.SUCCESS -> if (transaction != null) {
                    val cb = transactions[transaction]
                    LogUtil.d("Received message SUCCESS transaction = $transaction")
                    if (cb != null) {
                        try {
                            LogUtil.d("Received message cb = ${cb.feedId}, ${cb.tid}")
                            if (cb.feedId != null) {
                                cb.onSuccess(obj, cb.feedId!!)
                            } else {
                                cb.onSuccess(obj)
                            }
                            transactions.remove(transaction)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                JanusMessageType.ERROR -> {
                    if (transaction != null) {
                        val cb = transactions[transaction]
                        if (cb != null) {
                            cb.onError()
                            transactions.remove(transaction)
                        }
                    }
                }
                JanusMessageType.HANGUP -> {
                }
                JanusMessageType.DETACHED -> {
                    if (handle != null) {
                        if (janusCallback != null) {
                            janusCallback!!.onDetached(handle.handleId)
                        }
                    }
                }
                JanusMessageType.EVENT -> {
                    if (handle != null) {
                        var pluginData
                        : JSONObject? = null
                        if (obj.has("plugindata")) {
                            pluginData = obj.getJSONObject("plugindata")
                        }
                        if (pluginData != null) {
                            var data: JSONObject? = null
                            var jsep: JSONObject? = null
                            if (pluginData.has("data")) {
                                data = pluginData.getJSONObject("data")
                            }
                            if (obj.has("jsep")) {
                                jsep = obj.getJSONObject("jsep")
                            }
                            if (janusCallback != null) {
                                janusCallback!!.onMessage(
                                    sender,
                                    handle.handleId,
                                    data,
                                    jsep
                                )
                            }
                        }
                    }
                    if (handle != null) {
                        if (obj.has("candidate")) {
                            val candidate = obj.getJSONObject("candidate")
                            if (janusCallback != null) {
                                janusCallback!!.onIceCandidate(handle.handleId, candidate)
                            }
                        }
                    }
                }
                JanusMessageType.TRICKLE -> if (handle != null) {
                    if (obj.has("candidate")) {
                        val candidate = obj.getJSONObject("candidate")
                        if (janusCallback != null) {
                            janusCallback!!.onIceCandidate(handle.handleId, candidate)
                        }
                    }
                }
                JanusMessageType.DESTROY -> if (janusCallback != null) {
                    janusCallback!!.onDestroySession(sessionId)
                }
                else -> {
                    LogUtil.d(message)
                }
            }
        } catch (ex: JSONException) {
            if (janusCallback != null) {
                janusCallback!!.onError(ex.message)
            }
        }
    }

    /**
     * 连接关闭
     */
    override fun onClosed() {
        stopKeepAliveTimer()
    }

    /**
     * 启动KeepAlive
     */
    private fun startKeepAliveTimer() {
        isKeepAliveRunning = true
        keepAliveJob = GlobalScope.launch {
            while (isKeepAliveRunning) {
                delay(25000)
                if (webSocketChannel.isConnected()) {
                    val obj = JSONObject()
                    try {
                        obj.put("janus", "keepalive")
                        obj.put("session_id", sessionId)
                        obj.put("transaction", randomString(12))
                        webSocketChannel.sendMessage(obj.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    LogUtil.e("KeepAlive failed WebSocket is null or not connected")
                }
            }
        }
    }

    /**
     * 暂停KeepAlive
     */
    private fun stopKeepAliveTimer() {
        isKeepAliveRunning = false
        keepAliveJob?.let {
            if (!it.isCancelled) {
                it.cancel(CancellationException("stopKeepAliveTimer"))
            }
        }
    }


    private fun randomString(length: Int): String {
        val str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val random = Random()
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            sb.append(str[random.nextInt(str.length)])
        }
        return sb.toString()
    }
}