/**
 * Created with JackHou
 * Date: 2021/6/11
 * Time: 10:25
 * Description:Janus Video Item
 */

package com.qtimes.jetpackdemokotlin.ui.views

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qtimes.jetpackdemokotlin.R
import org.webrtc.EglBase
import org.webrtc.PeerConnection
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack
import java.math.BigInteger

class JanusVideoItem(var userId: BigInteger?, var display: String?) {
    var peerConnection: PeerConnection? = null
    var videoTrack: VideoTrack? = null
    var surfaceViewRenderer: SurfaceViewRenderer? = null
}

class JanusVideoItemHolder(eglBaseContext: EglBase.Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    var surfaceViewRenderer: SurfaceViewRenderer =
        itemView.findViewById(R.id.surface_view_render)
    var tvUserId: TextView = itemView.findViewById(R.id.tv_userid)
    var tvMute: TextView = itemView.findViewById(R.id.tv_mute)
    var tvSwitchCamera: TextView = itemView.findViewById(R.id.tv_switch_camera)

    init {
        surfaceViewRenderer.init(eglBaseContext, null)
    }
}
