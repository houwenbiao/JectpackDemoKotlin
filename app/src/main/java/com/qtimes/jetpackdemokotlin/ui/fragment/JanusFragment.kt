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


class JanusFragment : BaseFragment() {
    lateinit var binding: FragmentJanusBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.jumpVideoRoom.setOnClickListener {
            mNavController.navigate(JanusFragmentDirections.actionJanusFragmentToVideoRoomFragment())
        }

        binding.jumpVideoCall.setOnClickListener {
            mNavController.navigate(JanusFragmentDirections.actionJanusFragmentToVideoCallFragment())
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_janus
    }

    override fun bindingSetViewModels() {
        binding = viewDataBinding as FragmentJanusBinding
    }
}