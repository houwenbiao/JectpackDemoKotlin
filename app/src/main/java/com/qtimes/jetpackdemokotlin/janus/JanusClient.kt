/**
 * Created with JackHou
 * Date: 2021/5/19
 * Time: 14:28
 * Description:JanusClient
 * 参考1：https://blog.csdn.net/Java_lilin/article/details/104007291
 * 参考2：https://github.com/benwtrent/janus-gateway-android
 * 参考3：https://zhuanlan.zhihu.com/p/149324861?utm_source=wechat_session
 */

package com.qtimes.jetpackdemokotlin.janus

import android.content.Context
import android.widget.Toast
import com.qtimes.jetpackdemokotlin.common.MainApplication
import com.qtimes.jetpackdemokotlin.model.JanusMsgType
import com.qtimes.jetpackdemokotlin.model.PluginHandle
import com.qtimes.jetpackdemokotlin.model.Transaction
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import kotlinx.coroutines.*
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import java.math.BigInteger
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap

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


    /**
     * 销毁session
     */
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
                jsep.putOpt("type", sdp.type.canonicalForm())
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
            jsep.putOpt("type", sdp.type.canonicalForm())
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
     * VideoCall Hangup
     */
    fun hangup(handleId: BigInteger?) {
        val tid: String = randomString(12)
        try {
            val obj = JSONObject()
            val msg = JSONObject()
            msg.put("request", "hangup")
            obj.put("janus", "message")
            obj.put("transaction", tid)
            obj.put("session_id", sessionId)
            obj.put("handle_id", handleId)
            obj.put("body", msg)
            webSocketChannel.sendMessage(obj.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
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

    /**
     * VideoRoom JoinRoom
     */
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
     * VideoCall 注册
     */
    fun register(handleId: BigInteger?, userName: String?) {
        val message = JSONObject()
        val body = JSONObject()
        try {
            body.putOpt("request", "register")
            body.putOpt("username", userName)
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
     * VideoCall make call
     */
    fun makeCall(handleId: BigInteger?, sdp: SessionDescription, name: String) {
        val message = JSONObject()
        val body = JSONObject()
        try {
            body.putOpt("request", "call")
            body.putOpt("username", name)
            val jsep = JSONObject()
            jsep.putOpt("type", sdp.type.canonicalForm())
            jsep.putOpt("sdp", sdp.description)
            message.put("body", body)
            message.putOpt("jsep", jsep)
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
     * VideoCall get user list
     */
    fun list(handleId: BigInteger) {
        val message = JSONObject()
        val body = JSONObject()
        try {
            body.putOpt("request", "list")
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
     * 接收邀请
     */
    fun accept(handleId: BigInteger, sdp: SessionDescription) {
        val message = JSONObject()
        val body = JSONObject()
        try {
            body.putOpt("request", "accept")
            val jsep = JSONObject()
            jsep.putOpt("type", sdp.type.canonicalForm())
            jsep.putOpt("sdp", sdp.description)
            message.put("body", body)
            message.putOpt("jsep", jsep)
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
        LogUtil.d(">>>>>>>>>> WebSocket received msg <<<<<<<<< \n $message")
        try {
            val obj = JSONObject(message)
            val type: JanusMsgType = JanusMsgType.fromString(obj.getString("janus"))
            var transaction: String? = null
            var sender = BigInteger("0")
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
//            LogUtil.d("Received message type=$type transaction = $transaction, sender = $sender")
            when (type) {
                JanusMsgType.KEEPALIVE -> {
                }
                JanusMsgType.ACK -> {
                }
                JanusMsgType.SUCCESS -> if (transaction != null) {
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
                JanusMsgType.ERROR -> {
                    if (transaction != null) {
                        val cb = transactions[transaction]
                        if (cb != null) {
                            cb.onError()
                            transactions.remove(transaction)
                        }
                    }
                }
                JanusMsgType.HANGUP -> {
                    janusCallback?.onHangup(handle!!.handleId)
                }
                JanusMsgType.DETACHED -> {
                    if (handle != null) {
                        if (janusCallback != null) {
                            janusCallback!!.onDetached(handle.handleId)
                        }
                    }
                }
                JanusMsgType.EVENT -> {
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
                JanusMsgType.TRICKLE -> if (handle != null) {
                    if (obj.has("candidate")) {
                        val candidate = obj.getJSONObject("candidate")
                        if (janusCallback != null) {
                            janusCallback!!.onIceCandidate(handle.handleId, candidate)
                        }
                    }
                }
                JanusMsgType.DESTROY -> if (janusCallback != null) {
                    janusCallback!!.onDestroySession(sessionId)
                }
                else -> {
                    LogUtil.d("Other msg: $message")
                }
            }
        } catch (e: JSONException) {
            if (janusCallback != null) {
                janusCallback!!.onError(e.message)
            }
        }
    }

    override fun onFailure(response: Response?) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(MainApplication.context, "连接失败！", Toast.LENGTH_LONG).show()
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

    fun createVideoCapturer(context: Context, isFront: Boolean): VideoCapturer? {
        return if (Camera2Enumerator.isSupported(context)) {
            createCameraCapturer(Camera2Enumerator(context), isFront)
        } else {
            createCameraCapturer(Camera1Enumerator(true), isFront)
        }
    }

    private fun createCameraCapturer(
        enumerator: CameraEnumerator,
        isFront: Boolean
    ): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        if (isFront) {
            for (deviceName in deviceNames) {
                if (enumerator.isFrontFacing(deviceName)) {
                    val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
        } else {
            for (deviceName in deviceNames) {
                if (!enumerator.isFrontFacing(deviceName)) {
                    val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
        }
        return null
    }
}