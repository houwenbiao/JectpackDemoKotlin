/**
 * Created with JackHou
 * Date: 2021/5/21
 * Time: 11:19
 * Description:VideoRoom界面
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.audio.Player
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
import com.qtimes.jetpackdemokotlin.utils.AndroidUtil
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import com.qtimes.jetpackdemokotlin.viewmodel.VideoRoomViewModel
import kotlinx.coroutines.delay
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.IOException
import java.math.BigInteger

class VideoCallFragment : BaseFragment(), JanusCallback, CreatePeerConnectionCallback,
    SurfaceHolder.Callback {

    companion object {
        const val TAG: String = "VideoCallFragment"
        const val BUILDING_DOOR: Boolean = false//是否是楼宇门端
    }

    val videoRoomViewModel: VideoRoomViewModel by getViewModel(VideoRoomViewModel::class.java)
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private lateinit var binding: FragmentVideocallBinding

    private var ijkMediaPlayer: IjkMediaPlayer? = null
    private lateinit var inputMethodManager: InputMethodManager

    private lateinit var audioTrack: AudioTrack
    private var mPeerConnection: PeerConnection? = null
    private var videoTrack: VideoTrack? = null
    private var videoCapturer: VideoCapturer? = null
    private var videoSource: VideoSource? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null
    var eglBaseContext: EglBase.Context? = null
    private lateinit var janusClient: JanusClient
    var videoCallHandlerId: BigInteger = BigInteger("0")
    private var isFrontCamera: Boolean = false

    private var videoItemList: MutableList<JanusVideoItem> = arrayListOf()
    private var adapter: VideoItemAdapter? = null
    private var comingCallJsep: JSONObject? = null
    private var comingCallName: String? = null

    private var videoItemLocal: JanusVideoItem? = null
    private var videoItemRemote: JanusVideoItem? = null
    private var mPlayer: Player? = null

    //    private var mCallingName = "2545c4bc9de9be93"
    private var mCallingName = "3dde9b423f10e3c4"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (!BUILDING_DOOR) {
            binding.btnFaceRecognition.visibility = View.GONE
            binding.btnVideoCall.visibility = View.GONE
        }


        binding.btnHangup.setOnClickListener {
            janusClient.hangup(videoCallHandlerId)
            launchCPU {
                delay(500)
                releaseJanus()
                delay(2500)
                launchMain {
                    initJanus()
                }
            }
            launchMain {
                mPlayer?.stop()
                binding.clVideoComing.visibility = View.GONE
            }
        }

        binding.btnFaceRecognition.setOnClickListener {
            startLocalApp("org.tensorflow.lite.examples.detection")
        }

        binding.btnViewVideo.setOnClickListener {
            acceptCall(comingCallJsep)
            launchMain {
                mPlayer?.stop()
                binding.clVideo.visibility = View.VISIBLE
                binding.clVideoComing.visibility = View.GONE
                binding.clMain.visibility = View.INVISIBLE
            }
        }

        binding.btnCancelCalling.setOnClickListener {
            janusClient.hangup(videoCallHandlerId)
            launchMain {
                binding.clVideoComing.visibility = View.GONE
                binding.clVideoCalling.visibility = View.INVISIBLE
                binding.clMain.visibility = View.VISIBLE
                ijkMediaPlayer?.start()
            }
        }

        binding.btnVideoCall.setOnClickListener {
            binding.clHomeNum.visibility = View.VISIBLE
            binding.inputHomeNum.isFocusable = true
            binding.inputHomeNum.isFocusableInTouchMode = true
            binding.inputHomeNum.requestFocus()
            inputMethodManager.showSoftInput(binding.inputHomeNum, 0)
        }

        binding.inputHomeNum.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.clHomeNum.visibility = View.GONE
                inputMethodManager.hideSoftInputFromWindow(
                    binding.inputHomeNum.applicationWindowToken,
                    0
                )
                makeCall()
            }
            false
        }

        binding.clMain.setOnClickListener {
            binding.clHomeNum.visibility = View.GONE
            inputMethodManager.hideSoftInputFromWindow(
                binding.inputHomeNum.applicationWindowToken,
                0
            )
        }

        eglBaseContext = EglBase.create().eglBaseContext
        binding.rvVideoCall.layoutManager = GridLayoutManager(mContext, 1)
        binding.ijkSurfaceView.holder.addCallback(this)
        initPlayer()
        janusClient = JanusClient(HttpConfig.JANUS_URL)
        janusClient.setJanusCallback(this)
        initJanus()
    }

    private fun startLocalApp(packageNameTarget: String) {
        if (AndroidUtil.appIsExist(packageNameTarget)) {
            val packageManager: PackageManager = mContext!!.getPackageManager()
            val intent = packageManager.getLaunchIntentForPackage(packageNameTarget)
            startActivity(intent)
        }
    }

    private fun initJanus() {
        videoCapturer = janusClient.createVideoCapturer(mContext!!, isFrontCamera)
        if (videoCapturer == null) {
            return
        }
        peerConnectionFactory = PeerConnectionUtil.createPeerConnectionFactory(eglBaseContext!!)
        val audioSource: AudioSource = peerConnectionFactory.createAudioSource(MediaConstraints())

        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
        videoCapturer?.let {
            videoSource = peerConnectionFactory.createVideoSource(it.isScreencast)
            it.initialize(surfaceTextureHelper, mContext, videoSource!!.capturerObserver)
        }
        audioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)
        audioTrack.setEnabled(true)

        LogUtil.i("============$")
        videoTrack = peerConnectionFactory.createVideoTrack("102", videoSource)
        videoTrack?.setEnabled(true)

        janusClient.connect()

        mPeerConnection = PeerConnectionUtil.createPeerConnection(peerConnectionFactory, this)
        mPeerConnection?.addTrack(audioTrack)
        mPeerConnection?.addTrack(videoTrack)
        adapter = VideoItemAdapter()
        binding.rvVideoCall.adapter = adapter
        videoCapturer?.startCapture(
            Const.VIEW_HEIGHT_DEFAULT,
            Const.VIEW_WIDTH_DEFAULT,
            Const.VIDEO_FPS_DEFAULT
        )
    }

    override fun bindingSetViewModels() {
        super.bindingSetViewModels()
        binding = viewDataBinding as FragmentVideocallBinding
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_videocall
    }

    override fun onResume() {
        super.onResume()
        videoCapturer?.startCapture(
            Const.VIEW_HEIGHT_DEFAULT,
            Const.VIEW_WIDTH_DEFAULT,
            Const.VIDEO_FPS_DEFAULT
        )
    }

    override fun onPause() {
        super.onPause()
        ijkMediaPlayer?.pause()
    }

    private fun releaseJanus() {
        videoCapturer?.dispose()
        surfaceTextureHelper?.dispose()
        janusClient.disconnect()
        videoSource?.dispose()
        videoTrack?.dispose()
        mPeerConnection?.close()
        videoItemList.stream().forEach {
            it.peerConnection?.close()
            it.videoTrack?.dispose()
            it.surfaceViewRenderer?.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.release()
        ijkMediaPlayer?.release()
        releaseJanus()
    }

    /**
     * 呼叫对方
     */
    private fun makeCall() {
        PeerConnectionUtil.createOffer(mPeerConnection!!, object : CreateOfferCallback {
            override fun onCreateOfferSuccess(sdp: SessionDescription) {
                janusClient.makeCall(videoCallHandlerId, sdp, mCallingName)
                launchMain {
                    ijkMediaPlayer?.pause()
                    binding.clVideoCalling.visibility = View.VISIBLE
                    binding.clMain.visibility = View.INVISIBLE
                    binding.tvCallingName.text = "正在呼叫${binding.inputHomeNum.text.toString()}..."
                }
            }

            override fun onCreateFailed(error: String) {
                LogUtil.e("Create offer failed")
            }
        })
    }

    /**
     * 接受对方呼叫
     */
    private fun acceptCall(jsep: JSONObject?) {
        if (jsep != null) {
            val sdp = jsep.getString(JanusJsonKey.SDP.canonicalForm())
            // 添加用户到界面
            videoItemRemote = addNewVideoItem(
                null,
                comingCallName
            )
            launchMain { adapter!!.notifyItemInserted(videoItemList.size - 1) }

            videoItemRemote?.peerConnection = mPeerConnection
            mPeerConnection?.setRemoteDescription(
                object : SdpObserver {
                    override fun onCreateSuccess(sdp: SessionDescription) {

                    }

                    override fun onSetSuccess() {
                        if (BUILDING_DOOR) {
                            launchMain {
                                LogUtil.d("videoCapturer: $videoCapturer")

                                videoItemLocal =
                                    addNewVideoItem(
                                        null,
                                        videoRoomViewModel.userName.value
                                    )
                                videoItemLocal?.peerConnection = mPeerConnection
                                videoItemLocal?.videoTrack = videoTrack!!
                                LogUtil.d("videoItem: ${videoItemRemote?.videoTrack}, videoTrack: $videoTrack")
                                adapter!!.notifyItemInserted(videoItemList.size - 1)
                            }
                        }


                        PeerConnectionUtil.createAnswer(
                            mPeerConnection!!,
                            object : CreateAnswerCallback {
                                override fun onSetAnswerSuccess(sdp: SessionDescription) {
                                    janusClient.accept(videoCallHandlerId, sdp)
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

    override fun onSlowLink(handleId: BigInteger?) {

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

        try {
            val result: JSONObject? = data.getJSONObject(JanusJsonKey.RESULT.canonicalForm())
            if (result != null) {
                val event: String = result.getString(JanusJsonKey.EVENT.canonicalForm())
                when (JanusMsgType.fromString(event)) {
                    JanusMsgType.REGISTERED -> {
                        janusClient.list(videoCallHandlerId)
                    }

                    JanusMsgType.CALLING -> {
                        LogUtil.d("calling--------")
                    }

                    JanusMsgType.UPDATE -> {
                        LogUtil.e("==========update=========")
                    }

                    JanusMsgType.ACCEPTED -> {//主动呼叫对方然后收到对方接受的答复
                        if (jsep != null) {
                            val sdp = jsep.getString(JanusJsonKey.SDP.canonicalForm())
                            // 添加远程用户到界面
                            if (!BUILDING_DOOR) {
                                videoItemRemote = addNewVideoItem(
                                    null,
                                    result.getString(JanusJsonKey.USERNAME.canonicalForm())
                                )
                                launchMain { adapter!!.notifyItemInserted(videoItemList.size - 1) }
                                videoItemRemote?.peerConnection = mPeerConnection
                            }
                            mPeerConnection?.setRemoteDescription(
                                object : SdpObserver {
                                    override fun onCreateSuccess(sdp: SessionDescription) {
                                        LogUtil.d("=====onCreateSuccess=====")
                                    }

                                    override fun onSetSuccess() {
                                        LogUtil.d("=====onSetSuccess=====")

                                        launchMain {
                                            LogUtil.d("onSetSuccess videoCapturer: $videoCapturer")
                                            videoItemLocal =
                                                addNewVideoItem(
                                                    null,
                                                    videoRoomViewModel.userName.value
                                                )
                                            videoItemLocal?.videoTrack = videoTrack
                                            videoItemLocal?.peerConnection = mPeerConnection
                                            adapter!!.notifyItemInserted(videoItemList.size - 1)
                                        }
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
                        launchMain {
                            binding.clVideo.visibility = View.VISIBLE
                            binding.clVideoCalling.visibility = View.INVISIBLE
                        }
                    }

                    JanusMsgType.EVENT -> {

                    }

                    JanusMsgType.INCOMINGCALL -> {
                        launchMain {
                            mPlayer?.play(R.raw.call_coming)
                            binding.clMain.visibility = View.INVISIBLE
                            binding.clVideoComing.visibility = View.VISIBLE
                            ijkMediaPlayer?.pause()
                        }
                        comingCallJsep = jsep
                        comingCallName = result.getString(JanusJsonKey.USERNAME.canonicalForm())
                    }

                    JanusMsgType.SLOW_LINK -> {
                        LogUtil.w("===========SLOW_LINK===========")
                        launchMain {
                            showToast("网速慢......")
                        }
                    }

                    JanusMsgType.HANGUP -> {
                        LogUtil.w("===========HANGUP===========")
                        launchMain {
                            showToast("已挂断！")
                            mPlayer?.stop()
                            binding.clVideo.visibility = View.INVISIBLE
                            binding.clMain.visibility = View.VISIBLE
                            binding.clVideoCalling.visibility = View.INVISIBLE
                            binding.clVideoComing.visibility = View.GONE
                            val it: MutableIterator<JanusVideoItem> = videoItemList.iterator()
                            var index = 0
                            while (it.hasNext()) {
                                val item = it.next()
                                it.remove()
                                adapter?.notifyItemRemoved(index)
                                index++
                            }
                            ijkMediaPlayer?.start()
                            launchCPU {
                                delay(500)
                                releaseJanus()
                                delay(2500)
                                launchMain {
                                    initJanus()
                                }
                            }
                        }
                    }

                    else -> {

                    }
                }
            }
        } catch (e: JSONException) {
            val error: String? = data.getString("error")
            error?.let {
                launchMain {
                    binding.tvCallingName.text = "呼叫失败！"
                    launchCPU {
                        delay(2000)
                        launchMain {
                            binding.clVideoCalling.visibility = View.INVISIBLE
                            binding.clMain.visibility = View.VISIBLE
                            ijkMediaPlayer?.start()
                        }
                    }
                }
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
            itemHolder.btnEntrydoorHangup.setOnClickListener {
                janusClient.hangup(videoCallHandlerId)
            }

            itemHolder.btnOpenDoor.setOnClickListener {
                launchMain {
                    showToast("门已打开")
                }
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

    private fun initPlayer() {
        mPlayer = Player(mContext!!)
        ijkMediaPlayer = IjkMediaPlayer()
        try {
            val path = Environment.getExternalStorageDirectory().path + "/Movies/299495755-1-80.flv"
            ijkMediaPlayer?.dataSource = path
        } catch (e: IOException) {
            e.printStackTrace()
        }
        ijkMediaPlayer?.isLooping = true
        ijkMediaPlayer?.prepareAsync()
        ijkMediaPlayer?.start()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        //将所播放的视频图像输出到指定的SurfaceView组件
        ijkMediaPlayer?.setDisplay(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        ijkMediaPlayer?.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }
}