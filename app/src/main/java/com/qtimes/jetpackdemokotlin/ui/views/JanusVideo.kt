/**
 * Created with JackHou
 * Date: 2021/6/11
 * Time: 10:25
 * Description:Janus Video Item
 */

package com.qtimes.jetpackdemokotlin.ui.views

import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.ui.fragment.VideoCallFragment
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

class JanusVideoItemHolder(eglBaseContext: EglBase.Context, itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var surfaceViewRenderer: SurfaceViewRenderer =
        itemView.findViewById(R.id.surface_view_render)
    var tvUserId: TextView = itemView.findViewById(R.id.tv_userid)
    var tvMute: TextView = itemView.findViewById(R.id.tv_mute)
    var tvSwitchCamera: TextView = itemView.findViewById(R.id.tv_switch_camera)
    var rlBuildingDoorTalking: RelativeLayout = itemView.findViewById(R.id.rl_buildingdoor_talking)
    var rlEntryDoorTalking: ConstraintLayout = itemView.findViewById(R.id.rl_entrydoor_talking)
    var btnEntrydoorHangup: Button = itemView.findViewById(R.id.btn_entrydoor_hangup)

    init {
        surfaceViewRenderer.init(eglBaseContext, null)
        if (VideoCallFragment.BUILDING_DOOR) {
            rlBuildingDoorTalking.visibility = View.VISIBLE
            rlEntryDoorTalking.visibility = View.INVISIBLE
        } else {
            rlBuildingDoorTalking.visibility = View.INVISIBLE
            rlEntryDoorTalking.visibility = View.VISIBLE
        }
    }
}
