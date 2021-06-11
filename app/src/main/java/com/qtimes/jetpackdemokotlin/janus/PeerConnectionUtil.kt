/**
 * Created with JackHou
 * Date: 2021/6/11
 * Time: 10:48
 * Description:
 */

package com.qtimes.jetpackdemokotlin.janus

import com.qtimes.jetpackdemokotlin.common.MainApplication
import com.qtimes.jetpackdemokotlin.net.HttpConfig
import com.qtimes.jetpackdemokotlin.ui.fragment.VideoCallFragment
import com.qtimes.jetpackdemokotlin.ui.fragment.VideoRoomFragment
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import org.webrtc.*

/**
 * Author: JackHou
 * Date: 2021/6/11.
 */
class PeerConnectionUtil {
    companion object {
        fun createPeerConnectionFactory(eglBaseContext: EglBase.Context): PeerConnectionFactory {
            val encoderFactory: VideoEncoderFactory =
                DefaultVideoEncoderFactory(eglBaseContext, false, true)
            val decoderFactory: VideoDecoderFactory = DefaultVideoDecoderFactory(eglBaseContext)
            PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(MainApplication.context)
                    .setEnableInternalTracer(true)
                    .createInitializationOptions()
            )

            val builder = PeerConnectionFactory.builder().setVideoDecoderFactory(decoderFactory)
                .setVideoEncoderFactory(encoderFactory).setOptions(null)
            return builder.createPeerConnectionFactory()
        }


        fun createPeerConnection(
            peerConnectionFactory: PeerConnectionFactory,
            callback: CreatePeerConnectionCallback?
        ): PeerConnection? {
            val iceServerList: MutableList<PeerConnection.IceServer> = mutableListOf()
            iceServerList.add(PeerConnection.IceServer(HttpConfig.JANUS_ICE_URL))
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
                            callback?.onIceGatheringComplete()
                        }
                    }

                    override fun onIceCandidate(p0: IceCandidate) {
                        callback?.onIceCandidate(p0)
                    }

                    override fun onIceCandidatesRemoved(p0: Array<IceCandidate?>) {
                        callback?.onIceCandidatesRemoved(p0)
                    }

                    override fun onAddStream(p0: MediaStream) {
                        callback?.onAddStream(p0)
                    }

                    override fun onRemoveStream(p0: MediaStream) {
                        callback?.onRemoveStream(p0)
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

        fun createAnswer(peerConnection: PeerConnection, callback: CreateAnswerCallback?) {
            val mediaConstraints = MediaConstraints()
            mediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveAudio",
                    "true"
                )
            )
            mediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveVideo",
                    "true"
                )
            )
            peerConnection.createAnswer(object : SdpObserver {
                override fun onCreateSuccess(sdp: SessionDescription) {
                    peerConnection.setLocalDescription(object : SdpObserver {
                        override fun onCreateSuccess(sdp: SessionDescription) {}
                        override fun onSetSuccess() {
                            // send answer sdp
                            LogUtil.i("${VideoCallFragment.TAG} createAnswer onSetSuccess")
                            callback?.onSetAnswerSuccess(sdp)
                        }

                        override fun onCreateFailure(s: String) {
                            LogUtil.i("${VideoCallFragment.TAG} createAnswer onCreateFailure, $s")
                            callback?.onSetAnswerFailed(s)
                        }

                        override fun onSetFailure(s: String) {
                            LogUtil.i("${VideoCallFragment.TAG} createAnswer onSetFailure, $s")
                            callback?.onSetAnswerFailed(s)
                        }
                    }, sdp)
                }

                override fun onSetSuccess() {
                    LogUtil.i("${VideoCallFragment.TAG} createAnswer onSetSuccess")
                }

                override fun onCreateFailure(s: String) {
                    LogUtil.i("${VideoCallFragment.TAG} createAnswer onCreateFailure, $s")
                }

                override fun onSetFailure(s: String) {
                    LogUtil.i("${VideoCallFragment.TAG} createAnswer onSetFailure, $s")
                }
            }, mediaConstraints)
        }


        fun createOffer(peerConnection: PeerConnection, callback: CreateOfferCallback?) {
            val mediaConstraints = MediaConstraints()
            mediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveAudio",
                    "true"
                )
            )
            mediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveVideo",
                    "true"
                )
            )
            mediaConstraints.optional.add(
                MediaConstraints.KeyValuePair(
                    "DtlsSrtpKeyAgreement",
                    "true"
                )
            )
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
                    LogUtil.i("${VideoRoomFragment.TAG} createOffer onSetSuccess")
                }

                override fun onCreateFailure(error: String) {
                    LogUtil.i("${VideoRoomFragment.TAG} createOffer onCreateFailure, $error")
                    callback?.onCreateFailed(error)
                }

                override fun onSetFailure(error: String) {
                    LogUtil.i("${VideoRoomFragment.TAG} createOffer onSetFailure, $error")
                    callback?.onCreateFailed(error)
                }
            }, mediaConstraints)
        }
    }
}