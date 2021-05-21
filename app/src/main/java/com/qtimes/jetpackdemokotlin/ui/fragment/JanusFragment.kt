/**
 * Created with JackHou
 * Date: 2021/3/16
 * Time: 11:20
 * Description:
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.os.Bundle
import android.view.View
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.databinding.FragmentJanusBinding
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_janus.*


class JanusFragment : BaseFragment() {
    lateinit var fragmentJanusBinding: FragmentJanusBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        jump_video_room.setOnClickListener {
            mNavController.navigate(JanusFragmentDirections.actionJanusFragmentToVideoRoomFragment())
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_janus
    }

    override fun bindingSetViewModels() {
        fragmentJanusBinding = viewDataBinding as FragmentJanusBinding
    }
}