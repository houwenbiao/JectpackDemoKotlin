/**
 * Created with JackHou
 * Date: 2021/5/21
 * Time: 17:02
 * Description:Janus相关的接口
 */

package com.qtimes.jetpackdemokotlin.janus

import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.SessionDescription
import java.math.BigInteger


interface CreateAnswerCallback {
    fun onSetAnswerSuccess(sdp: SessionDescription)
    fun onSetAnswerFailed(error: String)
}

interface CreateOfferCallback {
    fun onCreateOfferSuccess(sdp: SessionDescription)
    fun onCreateFailed(error: String)
}

interface CreatePeerConnectionCallback {
    fun onIceGatheringComplete()
    fun onIceCandidate(candidate: IceCandidate)
    fun onIceCandidatesRemoved(candidates: Array<IceCandidate?>)
    fun onAddStream(stream: MediaStream)
    fun onRemoveStream(stream: MediaStream)
}

interface JanusCallback {

    fun onCreateSession(sessionId: BigInteger?)

    fun onJanusAttached(handleId: BigInteger)

    /**
     * 订阅回调
     *
     * @param subscribeHandleId 订阅HandlerId
     * @param feedId            订阅 feedId
     */
    fun onSubscribeAttached(subscribeHandleId: BigInteger, feedId: BigInteger)

    fun onDetached(handleId: BigInteger?)

    fun onHangup(handleId: BigInteger?)

    fun onMessage(
        sender: BigInteger,
        handleId: BigInteger,
        msg: JSONObject?,
        jsep: JSONObject?
    )

    fun onIceCandidate(handleId: BigInteger?, candidate: JSONObject?)

    fun onDestroySession(sessionId: BigInteger?)

    fun onError(error: String?)
}