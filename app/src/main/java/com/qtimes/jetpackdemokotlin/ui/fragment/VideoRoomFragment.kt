/**
 * Created with JackHou
 * Date: 2021/5/21
 * Time: 11:19
 * Description:VideoRoom界面
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.common.Const
import com.qtimes.jetpackdemokotlin.janus.*
import com.qtimes.jetpackdemokotlin.model.JanusMessageType
import com.qtimes.jetpackdemokotlin.model.JanusRoom
import com.qtimes.jetpackdemokotlin.model.Publisher
import com.qtimes.jetpackdemokotlin.net.HttpConfig
import com.qtimes.jetpackdemokotlin.net.HttpConfig.Companion.JANUS_ICE_URL
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import com.qtimes.jetpackdemokotlin.viewmodel.VideoRoomViewModel
import kotlinx.android.synthetic.main.fragment_videoroom.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import java.math.BigInteger

class VideoRoomFragment : BaseFragment(), JanusCallback, CreatePeerConnectionCallback {

    companion object {
        const val TAG: String = "VideoRoomFragment"
    }


    val videoRoomViewModel: VideoRoomViewModel by getViewModel(VideoRoomViewModel::class.java)
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private var peerConnection: PeerConnection? = null
    private lateinit var audioTrack: AudioTrack
    private var videoTrack: VideoTrack? = null
    private var videoCapturer: VideoCapturer? = null
    private var videoSource: VideoSource? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null
    var eglBaseContext: EglBase.Context? = null
    private lateinit var janusClient: JanusClient
    var videoRoomHandlerId: BigInteger = BigInteger("0")
    private var isFrontCamera: Boolean = false

    var room: JanusRoom = JanusRoom(1234) // 默认房间

    private var videoItemList: MutableList<VideoItem> = arrayListOf()
    private var adapter: VideoItemAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_vm.layoutManager = GridLayoutManager(mContext, 2)
        videoCapturer = createVideoCapturer(isFrontCamera)
        if (videoCapturer == null) {
            return
        }

        eglBaseContext = EglBase.create().eglBaseContext
        peerConnectionFactory = createPeerConnectionFactory()
        val audioSource: AudioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        audioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)
        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
        videoCapturer?.let {
            videoSource = peerConnectionFactory.createVideoSource(it.isScreencast)
            it.initialize(surfaceTextureHelper, mContext, videoSource!!.capturerObserver)
            it.startCapture(
                Const.VIEW_HEIGHT_DEFAULT,
                Const.VIEW_WIDTH_DEFAULT,
                Const.VIDEO_FPS_DEFAULT
            )
        }
        videoTrack = peerConnectionFactory.createVideoTrack("102", videoSource)

        janusClient = JanusClient(HttpConfig.JANUS_URL)
        janusClient.setJanusCallback(this)
        janusClient.connect()
        peerConnection = createPeerConnection(this)
        peerConnection?.addTrack(audioTrack)
        peerConnection?.addTrack(videoTrack)
        adapter = VideoItemAdapter()
        rv_vm.adapter = adapter
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_videoroom
    }

    override fun onDestroy() {
        super.onDestroy()
        videoCapturer?.dispose()
        surfaceTextureHelper?.dispose()
        janusClient?.disconnect()
        videoItemList.stream().forEach {
            it.peerConnection?.close()
            it.videoTrack?.dispose()
            it.surfaceViewRenderer?.release()
        }
    }

    private fun createPeerConnection(callback: CreatePeerConnectionCallback): PeerConnection? {
        val iceServerList: MutableList<PeerConnection.IceServer> = mutableListOf()
//        iceServerList.add(PeerConnection.IceServer("stun:stun.l.google.com:19302"))
//        iceServerList.add(PeerConnection.IceServer("stun:webrtc.encmed.cn:5349"))
        iceServerList.add(PeerConnection.IceServer(JANUS_ICE_URL))
        return peerConnectionFactory.createPeerConnection(
            iceServerList,
            object : PeerConnection.Observer {
                override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
                    LogUtil.d("onSignalingChange")
                }

                override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
                    LogUtil.d("onIceConnectionChange")
                }

                override fun onIceConnectionReceivingChange(p0: Boolean) {
                    LogUtil.d("onIceConnectionReceivingChange")
                }

                override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState) {
                    if (PeerConnection.IceConnectionState.COMPLETED.name == p0.name) {
                        callback.onIceGatheringComplete()
                    }
                }

                override fun onIceCandidate(p0: IceCandidate) {
                    callback.onIceCandidate(p0)
                }

                override fun onIceCandidatesRemoved(p0: Array<IceCandidate?>) {
                    callback.onIceCandidatesRemoved(p0)
                }

                override fun onAddStream(p0: MediaStream) {
                    callback.onAddStream(p0)
                }

                override fun onRemoveStream(p0: MediaStream) {
                    callback.onRemoveStream(p0)
                }

                override fun onDataChannel(p0: DataChannel) {
                    LogUtil.d("onDataChannel")
                }

                override fun onRenegotiationNeeded() {
                    LogUtil.d("onRenegotiationNeeded")
                }

                override fun onAddTrack(p0: RtpReceiver, p1: Array<out MediaStream>) {
                    LogUtil.d("onAddTrack")
                }
            })
    }


    private fun createPeerConnectionFactory(): PeerConnectionFactory {
        val encoderFactory: VideoEncoderFactory =
            DefaultVideoEncoderFactory(eglBaseContext, false, true)
        val decoderFactory: VideoDecoderFactory = DefaultVideoDecoderFactory(eglBaseContext)
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(mContext)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )

        val builder = PeerConnectionFactory.builder().setVideoDecoderFactory(decoderFactory)
            .setVideoEncoderFactory(encoderFactory).setOptions(null)
        return builder.createPeerConnectionFactory()
    }

    private fun createVideoCapturer(isFront: Boolean): VideoCapturer? {
        return if (Camera2Enumerator.isSupported(mContext)) {
            createCameraCapturer(Camera2Enumerator(mContext), isFront)
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


    inner class VideoItem(var userId: BigInteger?, var display: String?) {
        var peerConnection: PeerConnection? = null
        var videoTrack: VideoTrack? = null
        var surfaceViewRenderer: SurfaceViewRenderer? = null
    }

    inner class VideoItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var surfaceViewRenderer: SurfaceViewRenderer =
            itemView.findViewById(R.id.surface_view_render)
        var tvUserId: TextView = itemView.findViewById(R.id.tv_userid)
        var tvMute: TextView = itemView.findViewById(R.id.tv_mute)
        var tvSwitchCamera: TextView = itemView.findViewById(R.id.tv_switch_camera)
        init {
            surfaceViewRenderer.init(eglBaseContext, null)
        }
    }

    inner class VideoItemAdapter : RecyclerView.Adapter<VideoItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoItemHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.videoroom_item, parent, false)
            val itemHolder = VideoItemHolder(itemView)
            itemHolder.tvMute.setOnClickListener {
                val enable = audioTrack.enabled()
                if (enable) {
                    itemHolder.tvMute.text = "静音"
                } else {
                    itemHolder.tvMute.text = "取消静音"
                }
                audioTrack.setEnabled(!enable)
            }
            itemHolder.tvSwitchCamera.setOnClickListener {
                videoCapturer?.let { capturer ->
                    capturer.stopCapture()
                    capturer.dispose()
                    isFrontCamera = !isFrontCamera
                    videoCapturer = createVideoCapturer(isFrontCamera)
                    videoCapturer?.let {
                        it.initialize(surfaceTextureHelper, mContext, videoSource?.capturerObserver)
                        it.startCapture(
                            Const.VIEW_HEIGHT_DEFAULT,
                            Const.VIEW_WIDTH_DEFAULT,
                            Const.VIDEO_FPS_DEFAULT
                        )
                    }
                }
            }
            return itemHolder
        }

        override fun onBindViewHolder(holder: VideoItemHolder, position: Int) {
            val videoItem: VideoItem = videoItemList[position]
            videoItem.videoTrack?.addSink(holder.surfaceViewRenderer)
            videoItem.surfaceViewRenderer = holder.surfaceViewRenderer
            holder.tvUserId.text = videoItem.display
            if (videoRoomViewModel.userName.value == videoItem.display) {
                holder.tvMute.visibility = View.VISIBLE
                holder.tvSwitchCamera.visibility = View.VISIBLE
            } else {
                holder.tvMute.visibility = View.INVISIBLE
                holder.tvSwitchCamera.visibility = View.INVISIBLE
            }
        }

        override fun getItemCount(): Int {
            return videoItemList.size
        }
    }

    private fun createOffer(peerConnection: PeerConnection, callback: CreateOfferCallback?) {
        val mediaConstraints = MediaConstraints()
        //                mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
//                mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
//                mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        peerConnection.createOffer(object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription) {

                peerConnection.setLocalDescription(object : SdpObserver {
                    override fun onCreateSuccess(sdp: SessionDescription) {

                    }

                    override fun onSetSuccess() {

                    }

                    override fun onCreateFailure(error: String) {

                    }

                    override fun onSetFailure(error: String) {

                    }
                }, sdp)

//
                callback?.onCreateOfferSuccess(sdp)
            }

            override fun onSetSuccess() {
                LogUtil.i("$TAG createOffer onSetSuccess")
            }

            override fun onCreateFailure(error: String) {
                LogUtil.i("$TAG createOffer onCreateFailure, $error")
                callback?.onCreateFailed(error)
            }

            override fun onSetFailure(error: String) {
                LogUtil.i("$TAG createOffer onSetFailure, $error")
                callback?.onCreateFailed(error)
            }
        }, mediaConstraints)
    }


    private fun createAnswer(peerConnection: PeerConnection, callback: CreateAnswerCallback?) {
        val mediaConstraints = MediaConstraints()
        peerConnection.createAnswer(object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription) {
                peerConnection.setLocalDescription(object : SdpObserver {
                    override fun onCreateSuccess(sdp: SessionDescription) {}
                    override fun onSetSuccess() {
                        // send answer sdp
                        LogUtil.i("$TAG createAnswer onSetSuccess")
                        callback?.onSetAnswerSuccess(sdp)
                    }

                    override fun onCreateFailure(s: String) {
                        LogUtil.i("$TAG createAnswer onCreateFailure, $s")
                        callback?.onSetAnswerFailed(s)
                    }

                    override fun onSetFailure(s: String) {
                        LogUtil.i("$TAG createAnswer onSetFailure, $s")
                        callback?.onSetAnswerFailed(s)
                    }
                }, sdp)
            }

            override fun onSetSuccess() {
                LogUtil.i("$TAG createAnswer onSetSuccess")
            }

            override fun onCreateFailure(s: String) {
                LogUtil.i("$TAG createAnswer onCreateFailure, $s")
            }

            override fun onSetFailure(s: String) {
                LogUtil.i("$TAG createAnswer onSetFailure, $s")
            }
        }, mediaConstraints)
    }

    private fun handleNewPublishers(publishers: JSONArray) {
        for (i in 0 until publishers.length()) {
            try {
                val publishObj = publishers.getJSONObject(i)
                val feedId = BigInteger(publishObj.getString("id"))
                val display = publishObj.getString("display")
                // attach 到发布者的 handle 上
                janusClient.subscribeAttach(feedId)
                room.addPublisher(Publisher(feedId, display))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun addNewVideoItem(userId: BigInteger?, display: String?): VideoItem {
        val videoItem = VideoItem(userId, display)
        videoItemList.add(videoItem)
        return videoItem
    }


    /***************JanusCallback**************/
    override fun onCreateSession(sessionId: BigInteger?) {
        LogUtil.i("onCreateSession: $sessionId")
        janusClient.attachPlugin("janus.plugin.videoroom")
    }

    override fun onJanusAttached(handleId: BigInteger) {
        videoRoomHandlerId = handleId
        LogUtil.d("onJanusAttached: ${Thread.currentThread().name}")
        launchMain {
            janusClient.joinRoom(handleId, room.id, videoRoomViewModel.userName.value)
        }
    }

    override fun onSubscribeAttached(subscribeHandleId: BigInteger, feedId: BigInteger) {
        val publisher: Publisher = room.findPublisherById(feedId)
        publisher.handleId = subscribeHandleId
        janusClient.subscribe(subscribeHandleId, room.id, feedId)
    }

    override fun onDetached(handleId: BigInteger?) {
        videoRoomHandlerId = BigInteger("0")
    }

    override fun onHangup(handleId: BigInteger?) {

    }

    @ExperimentalStdlibApi
    override fun onMessage(
        sender: BigInteger,
        handleId: BigInteger,
        msg: JSONObject?,
        jsep: JSONObject?
    ) {
        if (msg == null) {
            return
        }
        if (!msg.has("videoroom")) {
            return
        }
        LogUtil.i("onMessage: $msg")
        val type: JanusMessageType = JanusMessageType.fromString(msg.getString("videoroom"))
        when (type) {
            JanusMessageType.JOINED -> {
                createOffer(peerConnection!!, object : CreateOfferCallback {
                    override fun onCreateOfferSuccess(sdp: SessionDescription) {
                        // 发布
                        janusClient.publish(videoRoomHandlerId, sdp)
                    }

                    override fun onCreateFailed(error: String) {
                        LogUtil.e("CreateOfferCallback onCreateFailed")
                    }

                })
                val publishers = msg.getJSONArray("publishers")
                handleNewPublishers(publishers)
            }

            JanusMessageType.EVENT -> {
                if (msg.has("configured") && msg.getString("configured") == "ok" && jsep != null) {
                    // sdp 协商成功，收到网关发来的 sdp answer
                    val sdp = jsep.getString("sdp")
                    peerConnection!!.setRemoteDescription(object : SdpObserver {
                        override fun onCreateSuccess(sdp: SessionDescription) {
                        }

                        override fun onSetSuccess() {
                            launchMain {
                                LogUtil.d("videoCapturer: $videoCapturer")
                                videoCapturer!!.startCapture(960, 540, 30)
                                val videoItem: VideoItem =
                                    addNewVideoItem(null, videoRoomViewModel.userName.value)
                                videoItem.peerConnection = peerConnection!!
                                videoItem.videoTrack = videoTrack!!
                                LogUtil.d("videoItem: ${videoItem.videoTrack}, videoTrack: $videoTrack")
                                adapter!!.notifyItemInserted(videoItemList.size - 1)
                            }
                        }

                        override fun onCreateFailure(error: String) {
                        }

                        override fun onSetFailure(error: String) {
                        }
                    }, SessionDescription(SessionDescription.Type.ANSWER, sdp))
                } else if (msg.has("unpublished")) {
                    val unPublishdUserId = msg.getLong("unpublished")
                } else if (msg.has("leaving")) {
                    // 离开
                    val leavingUserId = BigInteger(msg.getString("leaving"))
                    room.removePublisherById(leavingUserId)
                    launchMain {
                        val it: MutableIterator<VideoItem> = videoItemList.iterator()
                        var index = 0
                        while (it.hasNext()) {
                            val next = it.next()
                            if (leavingUserId == next.userId) {
                                it.remove()
                                adapter!!.notifyItemRemoved(index)
                            }
                            index++
                        }

                    }
                } else if (msg.has("publishers")) {
                    // 新用户开始发布
                    val publishers = msg.getJSONArray("publishers")
                    handleNewPublishers(publishers)
                } else if (msg.has("started") && msg.getString("started") == "ok") {
                    // 订阅 start 成功
                    LogUtil.i("订阅 start 成功")
                }
            }

            JanusMessageType.ATTACHED -> {
                if (jsep == null) {
                    return
                }
                // attach 到了一个Publisher 上,会收到网关转发来的sdp offer
                val sdp = jsep.getString("sdp")
                val feedId = BigInteger(msg.getString("id"))
                val display = msg.getString("display")
                val publisher = room.findPublisherById(feedId)

                // 添加用户到界面
                val videoItem = addNewVideoItem(feedId, display)
                launchMain { adapter!!.notifyItemInserted(videoItemList.size - 1) }

                val peerConnection = createPeerConnection(object : CreatePeerConnectionCallback {
                    override fun onIceGatheringComplete() {
                        janusClient.trickleCandidateComplete(sender)
                    }

                    override fun onIceCandidate(candidate: IceCandidate) {
                        janusClient.trickleCandidate(sender, candidate)
                    }

                    override fun onIceCandidatesRemoved(candidates: Array<IceCandidate?>) {}
                    override fun onAddStream(stream: MediaStream) {
                        if (stream.videoTracks.size > 0) {
                            launchMain {
                                videoItem.videoTrack = stream.videoTracks[0]
                                adapter!!.notifyDataSetChanged()
                            }
                        }
                    }

                    override fun onRemoveStream(stream: MediaStream) {

                    }
                })
                videoItem.peerConnection = peerConnection!!
                peerConnection.setRemoteDescription(object : SdpObserver {
                    override fun onCreateSuccess(sdp: SessionDescription) {

                    }

                    override fun onSetSuccess() {

                        // 这时应该回复网关一个 start ，附带自己的 sdp answer
                        createAnswer(peerConnection, object : CreateAnswerCallback {
                            override fun onSetAnswerSuccess(sdp: SessionDescription) {
                                janusClient.subscriptionStart(
                                    publisher.handleId,
                                    room.id,
                                    sdp
                                )
                            }

                            override fun onSetAnswerFailed(error: String) {

                            }
                        })
                    }

                    override fun onCreateFailure(error: String) {

                    }

                    override fun onSetFailure(error: String) {

                    }
                }, SessionDescription(SessionDescription.Type.OFFER, sdp))
            }

            else -> {
                LogUtil.d(msg)
            }
        }
    }

    override fun onIceCandidate(handleId: BigInteger?, candidate: JSONObject?) {

    }

    override fun onDestroySession(sessionId: BigInteger?) {
        sessionId?.let {
            LogUtil.i("onDestroySession(id =  $sessionId)")

        }
    }

    override fun onError(error: String?) {
        error?.let {
            LogUtil.e(it)
        }
    }
    /***************JanusCallback**************/


    /***********CreatePeerConnectionCallback**********/
    override fun onIceGatheringComplete() {
        janusClient.trickleCandidateComplete(videoRoomHandlerId)
    }

    override fun onIceCandidate(candidate: IceCandidate) {
        janusClient.trickleCandidate(videoRoomHandlerId, candidate)
    }

    override fun onIceCandidatesRemoved(candidates: Array<IceCandidate?>) {
        peerConnection?.removeIceCandidates(candidates)
    }

    override fun onAddStream(stream: MediaStream) {

    }

    override fun onRemoveStream(stream: MediaStream) {

    }
    /***********CreatePeerConnectionCallback**********/
}