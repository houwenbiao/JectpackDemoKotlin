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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.common.Const
import com.qtimes.jetpackdemokotlin.common.JanusPlugin
import com.qtimes.jetpackdemokotlin.databinding.FragmentVideocallBinding
import com.qtimes.jetpackdemokotlin.janus.*
import com.qtimes.jetpackdemokotlin.model.JanusJsonKey
import com.qtimes.jetpackdemokotlin.model.JanusMsgType
import com.qtimes.jetpackdemokotlin.net.HttpConfig
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.ui.views.JanusVideoItem
import com.qtimes.jetpackdemokotlin.ui.views.JanusVideoItemHolder
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import com.qtimes.jetpackdemokotlin.viewmodel.VideoRoomViewModel
import org.json.JSONObject
import org.webrtc.*
import java.math.BigInteger

class VideoCallFragment : BaseFragment(), JanusCallback, CreatePeerConnectionCallback {

    companion object {
        const val TAG: String = "VideoCallFragment"
    }

    val videoRoomViewModel: VideoRoomViewModel by getViewModel(VideoRoomViewModel::class.java)
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private lateinit var binding: FragmentVideocallBinding

    private lateinit var audioTrack: AudioTrack
    private var mPeerConnection: PeerConnection? = null
    private var videoTrack: VideoTrack? = null
    private var videoCapturer: VideoCapturer? = null
    private var videoSource: VideoSource? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null
    var eglBaseContext: EglBase.Context? = null
    private lateinit var janusClient: JanusClient
    var videoCallHandlerId: BigInteger = BigInteger("0")
    private var isFrontCamera: Boolean = true

    private var videoItemList: MutableList<JanusVideoItem> = arrayListOf()
    private var adapter: VideoItemAdapter? = null

    private var videoItemLocal: JanusVideoItem? = null
    private var videoItemRemote: JanusVideoItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eglBaseContext = EglBase.create().eglBaseContext
        binding.rvVideoCall.layoutManager = GridLayoutManager(mContext, 2)
        janusClient = JanusClient(HttpConfig.JANUS_URL)
        janusClient.setJanusCallback(this)
        videoCapturer = janusClient.createVideoCapturer(mContext!!, isFrontCamera)
        if (videoCapturer == null) {
            return
        }
        peerConnectionFactory = PeerConnectionUtil.createPeerConnectionFactory(eglBaseContext!!)
        val audioSource: AudioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        audioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)
        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
        videoCapturer?.let {
            videoSource = peerConnectionFactory.createVideoSource(it.isScreencast)
            it.initialize(surfaceTextureHelper, mContext, videoSource!!.capturerObserver)
        }
        videoTrack = peerConnectionFactory.createVideoTrack("102", videoSource)
        videoTrack?.setEnabled(true)
        janusClient.connect()

        mPeerConnection = PeerConnectionUtil.createPeerConnection(peerConnectionFactory, this)
        mPeerConnection?.addTrack(audioTrack)
        mPeerConnection?.addTrack(videoTrack)
        adapter = VideoItemAdapter()
        binding.rvVideoCall.adapter = adapter
    }

    override fun bindingSetViewModels() {
        super.bindingSetViewModels()
        binding = viewDataBinding as FragmentVideocallBinding
    }

    override fun onResume() {
        super.onResume()
        videoCapturer?.startCapture(
            Const.VIEW_HEIGHT_DEFAULT,
            Const.VIEW_WIDTH_DEFAULT,
            Const.VIDEO_FPS_DEFAULT
        )
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_videocall
    }

    override fun onDestroy() {
        super.onDestroy()
        videoCapturer?.dispose()
        surfaceTextureHelper?.dispose()
        janusClient.disconnect()
        videoItemList.stream().forEach {
            it.peerConnection?.close()
            it.videoTrack?.dispose()
            it.surfaceViewRenderer?.release()
        }
    }

    private fun makeCall() {
        PeerConnectionUtil.createOffer(mPeerConnection!!, object : CreateOfferCallback {
            override fun onCreateOfferSuccess(sdp: SessionDescription) {
                janusClient.makeCall(videoCallHandlerId, sdp, "2545c4bc9de9be93")
            }

            override fun onCreateFailed(error: String) {
                LogUtil.e("Create offer failed")
            }
        })
    }


    /***************JanusCallback**************/
    override fun onCreateSession(sessionId: BigInteger?) {
        LogUtil.i("onCreateSession: $sessionId")
        janusClient.attachPlugin(JanusPlugin.VIDEO_CALL)
    }

    override fun onJanusAttached(handleId: BigInteger) {
        LogUtil.d("onJanusAttached handleId: $handleId")
        videoCallHandlerId = handleId
        LogUtil.d("onJanusAttached: ${Thread.currentThread().name}")
        launchMain {
            janusClient.register(handleId, videoRoomViewModel.userName.value)
        }
    }

    override fun onSubscribeAttached(subscribeHandleId: BigInteger, feedId: BigInteger) {

    }

    override fun onDetached(handleId: BigInteger?) {
        LogUtil.d("onDetached handleId: $handleId")
        videoCallHandlerId = BigInteger("0")
    }

    override fun onHangup(handleId: BigInteger?) {
        LogUtil.d("onHangup handleId: $handleId")
    }

    @ExperimentalStdlibApi
    override fun onMessage(
        sender: BigInteger, handleId: BigInteger, data: JSONObject?, jsep: JSONObject?
    ) {
        if (data == null) {
            return
        }
        if (!data.has(JanusJsonKey.VIDEOCALL.canonicalForm())) {
            return
        }

        val result: JSONObject = data.getJSONObject(JanusJsonKey.RESULT.canonicalForm()) ?: return

        val event: String = result.getString(JanusJsonKey.EVENT.canonicalForm())

        when (JanusMsgType.fromString(event)) {

            JanusMsgType.REGISTERED -> {
                janusClient.list(videoCallHandlerId)
                makeCall()
            }

            JanusMsgType.CALLING -> {
                LogUtil.d("calling--------")
                launchMain {
                    showToast("正在呼叫......")
                    LogUtil.d("onSetSuccess videoCapturer: $videoCapturer")
                    videoItemLocal = addNewVideoItem(null, videoRoomViewModel.userName.value)
                    videoItemLocal?.peerConnection = mPeerConnection
                    videoItemLocal?.videoTrack = videoTrack
                    adapter!!.notifyItemInserted(videoItemList.size - 1)
                }
            }

            JanusMsgType.UPDATE -> {
                LogUtil.e("==========update=========")
            }

            JanusMsgType.ACCEPTED -> {
                if (jsep != null) {
                    val sdp = jsep.getString(JanusJsonKey.SDP.canonicalForm())
                    // 添加远程用户到界面
                    videoItemRemote = addNewVideoItem(
                        videoCallHandlerId,
                        result.getString(JanusJsonKey.USERNAME.canonicalForm())
                    )
                    launchMain { adapter!!.notifyItemInserted(videoItemList.size - 1) }
                    videoItemRemote?.peerConnection = mPeerConnection
                    mPeerConnection?.setRemoteDescription(
                        object : SdpObserver {
                            override fun onCreateSuccess(sdp: SessionDescription) {
                                LogUtil.d("=====onCreateSuccess=====")
                            }

                            override fun onSetSuccess() {
                                LogUtil.d("=====onSetSuccess=====")
                            }

                            override fun onCreateFailure(error: String) {
                                LogUtil.d("=====onCreateFailure=====")
                            }

                            override fun onSetFailure(error: String) {
                                LogUtil.d("=====onSetFailure=====")
                            }
                        }, SessionDescription(
                            SessionDescription.Type.ANSWER, sdp
                        )
                    )
                }
            }

            JanusMsgType.EVENT -> {
                LogUtil.d("event-------")
            }

            JanusMsgType.INCOMINGCALL -> {
                if (jsep != null) {
                    val sdp = jsep.getString(JanusJsonKey.SDP.canonicalForm())
                    // 添加用户到界面
                    val videoItemRemote = addNewVideoItem(
                        null,
                        result.getString(JanusJsonKey.USERNAME.canonicalForm())
                    )
                    launchMain { adapter!!.notifyItemInserted(videoItemList.size - 1) }

                    val peerConnection =
                        PeerConnectionUtil.createPeerConnection(
                            peerConnectionFactory,
                            object : CreatePeerConnectionCallback {
                                override fun onIceGatheringComplete() {
                                    janusClient.trickleCandidateComplete(sender)
                                }

                                override fun onIceCandidate(candidate: IceCandidate) {
                                    janusClient.trickleCandidate(sender, candidate)
                                }

                                override fun onIceCandidatesRemoved(candidates: Array<IceCandidate?>) {

                                }

                                override fun onAddStream(stream: MediaStream) {
                                    if (stream.videoTracks.size > 0) {
                                        launchMain {
                                            videoItemRemote.videoTrack = stream.videoTracks[0]
                                            adapter!!.notifyDataSetChanged()
                                        }
                                    }
                                }

                                override fun onRemoveStream(stream: MediaStream) {

                                }
                            })
                    peerConnection?.addTrack(audioTrack)
                    peerConnection?.addTrack(videoTrack)
                    videoItemRemote.peerConnection = peerConnection
                    peerConnection?.setRemoteDescription(
                        object : SdpObserver {
                            override fun onCreateSuccess(sdp: SessionDescription) {

                            }

                            override fun onSetSuccess() {
                                launchMain {
                                    LogUtil.d("videoCapturer: $videoCapturer")
                                    videoCapturer!!.startCapture(
                                        Const.VIEW_WIDTH_DEFAULT,
                                        Const.VIEW_HEIGHT_DEFAULT,
                                        Const.VIDEO_FPS_DEFAULT
                                    )
                                    val videoItemLocal: JanusVideoItem =
                                        addNewVideoItem(null, videoRoomViewModel.userName.value)
                                    videoItemLocal.peerConnection = peerConnection
                                    videoItemLocal.videoTrack = videoTrack!!
                                    LogUtil.d("videoItem: ${videoItemRemote.videoTrack}, videoTrack: $videoTrack")
                                    adapter!!.notifyItemInserted(videoItemList.size - 1)
                                }

                                PeerConnectionUtil.createAnswer(
                                    peerConnection,
                                    object : CreateAnswerCallback {
                                        override fun onSetAnswerSuccess(sdp: SessionDescription) {
                                            janusClient.accept(handleId, sdp)
                                        }

                                        override fun onSetAnswerFailed(error: String) {

                                        }
                                    })
                            }

                            override fun onCreateFailure(error: String) {

                            }

                            override fun onSetFailure(error: String) {

                            }
                        }, SessionDescription(
                            SessionDescription.Type.OFFER, sdp
                        )
                    )
                }
            }

            JanusMsgType.HANGUP -> {
                LogUtil.i("hangup------")
                launchMain {
                    val it: MutableIterator<JanusVideoItem> = videoItemList.iterator()
                    var index = 0
                    while (it.hasNext()) {
                        it.next()
                        it.remove()
                        adapter!!.notifyItemRemoved(index)
                        index++
                    }
                    janusClient.disconnect()
                }
                mNavController.navigateUp()
            }

            else -> {

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


    override fun onIceGatheringComplete() {
        janusClient.trickleCandidateComplete(videoCallHandlerId)
    }

    override fun onIceCandidate(candidate: IceCandidate) {
        janusClient.trickleCandidate(videoCallHandlerId, candidate)
    }

    override fun onIceCandidatesRemoved(candidates: Array<IceCandidate?>) {
    }

    override fun onAddStream(stream: MediaStream) {
        LogUtil.d("----------onAddStream---------")
        if (stream.videoTracks.size > 0) {
            launchMain {
                videoItemRemote?.videoTrack = stream.videoTracks[0]
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onRemoveStream(stream: MediaStream) {
    }

    inner class VideoItemAdapter : RecyclerView.Adapter<JanusVideoItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JanusVideoItemHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.videoroom_item, parent, false)
            val itemHolder = JanusVideoItemHolder(eglBaseContext!!, itemView)
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
                    videoCapturer = janusClient.createVideoCapturer(mContext!!, isFrontCamera)
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

        override fun onBindViewHolder(holder: JanusVideoItemHolder, position: Int) {
            val videoItem: JanusVideoItem = videoItemList[position]
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


    fun addNewVideoItem(userId: BigInteger?, display: String?): JanusVideoItem {
        val videoItem = JanusVideoItem(userId, display)
        videoItemList.add(videoItem)
        return videoItem
    }
}