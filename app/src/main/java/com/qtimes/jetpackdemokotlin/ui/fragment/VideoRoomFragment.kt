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
import com.qtimes.jetpackdemokotlin.janus.CreatePeerConnectionCallback
import com.qtimes.jetpackdemokotlin.janus.JanusCallback
import com.qtimes.jetpackdemokotlin.janus.JanusClient
import com.qtimes.jetpackdemokotlin.model.JanusRoom
import com.qtimes.jetpackdemokotlin.net.HttpConfig
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import com.qtimes.jetpackdemokotlin.viewmodel.VideoRoomViewModel
import kotlinx.android.synthetic.main.fragment_github_repository.*
import org.json.JSONObject
import org.webrtc.*
import java.math.BigInteger
import java.util.*
import kotlin.random.Random

class VideoRoomFragment : BaseFragment(), JanusCallback, CreatePeerConnectionCallback {
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
    private var isFrontCamera: Boolean = true

    var room: JanusRoom = JanusRoom(Random(1234567890).nextInt()) // 默认房间

    private var videoItemList: List<VideoItem> = ArrayList()
    private var adapter: VideoItemAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = GridLayoutManager(mContext, 2)
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
        recycler_view.adapter = adapter
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_videoroom
    }

    override fun onDestroy() {
        super.onDestroy()
        videoCapturer?.dispose()
        surfaceTextureHelper?.dispose()
        janusClient.disconnect()
        videoItemList.stream().forEach {
            it.peerConnection.close()
            it.videoTrack.dispose()
            it.surfaceViewRenderer.release()
        }
    }

    private fun createPeerConnection(callback: CreatePeerConnectionCallback): PeerConnection? {
        val iceServerList: MutableList<PeerConnection.IceServer> = mutableListOf()
        iceServerList.add(PeerConnection.IceServer("stun:stun.l.google.com:19302"))
        iceServerList.add(PeerConnection.IceServer("stun:webrtc.encmed.cn:5349"))
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


    inner class VideoItem(
        var peerConnection: PeerConnection,
        var userId: BigInteger,
        var display: String,
        var videoTrack: VideoTrack,
        var surfaceViewRenderer: SurfaceViewRenderer
    )

    inner class VideoItemHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {

        var surfaceViewRenderer: SurfaceViewRenderer =
            itemView.findViewById(R.id.surface_view_render)
        var tvUserId: TextView = itemView.findViewById(R.id.tv_userid)
        var tvMute: TextView = itemView.findViewById(R.id.tv_mute)
        var tvSwitchCamera: TextView = itemView.findViewById(R.id.tv_switch_camera)
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
            videoItem.videoTrack.addSink(holder.surfaceViewRenderer)
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

    override fun onCreateSession(sessionId: BigInteger?) {
        TODO("Not yet implemented")
    }

    override fun onAttached(handleId: BigInteger?) {
        TODO("Not yet implemented")
    }

    override fun onSubscribeAttached(subscribeHandleId: BigInteger?, feedId: BigInteger?) {
        TODO("Not yet implemented")
    }

    override fun onDetached(handleId: BigInteger?) {
        TODO("Not yet implemented")
    }

    override fun onHangup(handleId: BigInteger?) {
        TODO("Not yet implemented")
    }

    override fun onMessage(
        sender: BigInteger?,
        handleId: BigInteger?,
        msg: JSONObject?,
        jsep: JSONObject?
    ) {
        TODO("Not yet implemented")
    }

    override fun onIceCandidate(handleId: BigInteger?, candidate: JSONObject?) {
        TODO("Not yet implemented")
    }

    override fun onDestroySession(sessionId: BigInteger?) {
        TODO("Not yet implemented")
    }

    override fun onError(error: String?) {
        TODO("Not yet implemented")
    }

    override fun onIceGatheringComplete() {
        TODO("Not yet implemented")
    }

    override fun onIceCandidate(candidate: IceCandidate) {
        TODO("Not yet implemented")
    }

    override fun onIceCandidatesRemoved(candidates: Array<IceCandidate?>) {
        TODO("Not yet implemented")
    }

    override fun onAddStream(stream: MediaStream) {
        TODO("Not yet implemented")
    }

    override fun onRemoveStream(stream: MediaStream) {
        TODO("Not yet implemented")
    }
}