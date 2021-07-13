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
import com.qtimes.jetpackdemokotlin.databinding.FragmentVideoroomBinding
import com.qtimes.jetpackdemokotlin.janus.*
import com.qtimes.jetpackdemokotlin.janus.PeerConnectionUtil.Companion.createAnswer
import com.qtimes.jetpackdemokotlin.janus.PeerConnectionUtil.Companion.createOffer
import com.qtimes.jetpackdemokotlin.janus.PeerConnectionUtil.Companion.createPeerConnection
import com.qtimes.jetpackdemokotlin.janus.PeerConnectionUtil.Companion.createPeerConnectionFactory
import com.qtimes.jetpackdemokotlin.model.JanusJsonKey
import com.qtimes.jetpackdemokotlin.model.JanusMsgType
import com.qtimes.jetpackdemokotlin.model.JanusRoom
import com.qtimes.jetpackdemokotlin.model.Publisher
import com.qtimes.jetpackdemokotlin.net.HttpConfig
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.ui.views.JanusVideoItem
import com.qtimes.jetpackdemokotlin.ui.views.JanusVideoItemHolder
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import com.qtimes.jetpackdemokotlin.viewmodel.VideoRoomViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import java.math.BigInteger

class VideoRoomFragment : BaseFragment(), JanusCallback {

    companion object {
        const val TAG: String = "VideoRoomFragment"
    }

    private lateinit var binding: FragmentVideoroomBinding
    val videoRoomViewModel: VideoRoomViewModel by getViewModel(VideoRoomViewModel::class.java)
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private var mPeerConnection: PeerConnection? = null
    private lateinit var audioTrack: AudioTrack
    private var videoTrack: VideoTrack? = null
    private var videoCapturer: VideoCapturer? = null
    private var videoSource: VideoSource? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null
    var eglBaseContext: EglBase.Context? = null
    private lateinit var janusClient: JanusClient
    var videoRoomHandlerId: BigInteger = BigInteger("0")
    private var isFrontCamera: Boolean = true

    var room: JanusRoom = JanusRoom(1234) // 默认房间

    private var videoItemList: MutableList<JanusVideoItem> = arrayListOf()
    private var adapter: VideoItemAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eglBaseContext = EglBase.create().eglBaseContext
        janusClient = JanusClient(HttpConfig.JANUS_URL)
        janusClient.setJanusCallback(this)
        binding.rvVm.layoutManager = GridLayoutManager(mContext, 2)
        videoCapturer = janusClient.createVideoCapturer(mContext!!, isFrontCamera)
        if (videoCapturer == null) {
            return
        }
        peerConnectionFactory = createPeerConnectionFactory(eglBaseContext!!)
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
        janusClient.connect()
        mPeerConnection = createPeerConnection(peerConnectionFactory, null)
        mPeerConnection?.addTrack(audioTrack)
        mPeerConnection?.addTrack(videoTrack)
        adapter = VideoItemAdapter()
        binding.rvVm.adapter = adapter
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_videoroom
    }

    override fun bindingSetViewModels() {
        super.bindingSetViewModels()
        binding = viewDataBinding as FragmentVideoroomBinding
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

    inner class VideoItemAdapter : RecyclerView.Adapter<JanusVideoItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JanusVideoItemHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.videoroom_item, parent, false)
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

    fun addNewVideoItem(userId: BigInteger?, display: String?): JanusVideoItem {
        val videoItem = JanusVideoItem(userId, display)
        videoItemList.add(videoItem)
        return videoItem
    }


    /***************JanusCallback**************/
    override fun onCreateSession(sessionId: BigInteger?) {
        LogUtil.i("onCreateSession: $sessionId")
        janusClient.attachPlugin(JanusPlugin.VIDEO_ROOM)
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
        sender: BigInteger, handleId: BigInteger, msg: JSONObject?, jsep: JSONObject?
    ) {
        if (msg == null) {
            return
        }
        if (!msg.has(JanusJsonKey.VIDEOROOM.canonicalForm())) {
            return
        }
        LogUtil.i("onMessage: $msg")
        when (JanusMsgType.fromString(msg.getString(JanusJsonKey.VIDEOROOM.canonicalForm()))) {
            JanusMsgType.JOINED -> {
                createOffer(mPeerConnection!!, object : CreateOfferCallback {
                    override fun onCreateOfferSuccess(sdp: SessionDescription) {
                        // 发布
                        janusClient.publish(videoRoomHandlerId, sdp)
                    }

                    override fun onCreateFailed(error: String) {
                        LogUtil.e("CreateOfferCallback onCreateFailed")
                    }

                })
                val publishers = msg.getJSONArray(JanusJsonKey.PUBLISHERS.canonicalForm())
                handleNewPublishers(publishers)
            }

            JanusMsgType.EVENT -> {
                if (msg.has(JanusJsonKey.CONFIGURED.canonicalForm()) &&
                    msg.getString(JanusJsonKey.CONFIGURED.canonicalForm()) == "ok" && jsep != null
                ) {
                    // sdp 协商成功，收到网关发来的 sdp answer
                    val sdp = jsep.getString(JanusJsonKey.SDP.canonicalForm())
                    mPeerConnection!!.setRemoteDescription(object : SdpObserver {
                        override fun onCreateSuccess(sdp: SessionDescription) {

                        }

                        override fun onSetSuccess() {
                            launchMain {
                                LogUtil.d("videoCapturer: $videoCapturer")
                                videoCapturer!!.startCapture(1920, 1080, 30)
                                val videoItem: JanusVideoItem =
                                    addNewVideoItem(null, videoRoomViewModel.userName.value)
                                videoItem.peerConnection = mPeerConnection!!
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
                } else if (msg.has(JanusJsonKey.UNPUBLISHED.canonicalForm())) {
                    val unPublishedUserId =
                        BigInteger(msg.getString(JanusJsonKey.UNPUBLISHED.canonicalForm()))
                    LogUtil.i(unPublishedUserId)
                    room.removePublisherById(unPublishedUserId)
                    launchMain {
                        val it: MutableIterator<JanusVideoItem> = videoItemList.iterator()
                        var index = 0
                        while (it.hasNext()) {
                            val next = it.next()
                            if (unPublishedUserId == next.userId) {
                                it.remove()
                                adapter!!.notifyItemRemoved(index)
                            }
                            index++
                        }
                    }
                } else if (msg.has(JanusJsonKey.LEAVING.canonicalForm())) {
                    // 离开
                    val leavingUserId =
                        BigInteger(msg.getString(JanusJsonKey.LEAVING.canonicalForm()))
                    room.removePublisherById(leavingUserId)
                    launchMain {
                        val it: MutableIterator<JanusVideoItem> = videoItemList.iterator()
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
                } else if (msg.has(JanusJsonKey.PUBLISHERS.canonicalForm())) {
                    // 新用户开始发布
                    val publishers = msg.getJSONArray(JanusJsonKey.PUBLISHERS.canonicalForm())
                    handleNewPublishers(publishers)
                } else if (msg.has(JanusJsonKey.STARTED.canonicalForm()) &&
                    msg.getString(JanusJsonKey.STARTED.canonicalForm()) == "ok"
                ) {
                    // 订阅 start 成功
                    LogUtil.i("订阅 start 成功")
                }
            }

            JanusMsgType.ATTACHED -> {
                if (jsep == null) {
                    return
                }
                // attach 到了一个Publisher 上,会收到网关转发来的sdp offer
                val sdp = jsep.getString(JanusJsonKey.SDP.canonicalForm())
                val feedId = BigInteger(msg.getString(JanusJsonKey.ID.canonicalForm()))
                val display = msg.getString(JanusJsonKey.DISPLAY.canonicalForm())
                val publisher = room.findPublisherById(feedId)

                // 添加用户到界面
                val videoItem = addNewVideoItem(feedId, display)
                launchMain { adapter!!.notifyItemInserted(videoItemList.size - 1) }

                val peerConnection = createPeerConnection(
                    peerConnectionFactory,
                    object : CreatePeerConnectionCallback {
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
}