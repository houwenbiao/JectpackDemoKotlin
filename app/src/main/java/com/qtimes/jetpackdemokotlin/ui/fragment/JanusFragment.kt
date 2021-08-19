/**
 * Created with JackHou
 * Date: 2021/3/16
 * Time: 11:20
 * Description:
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.databinding.FragmentJanusBinding
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.IOException


class JanusFragment : BaseFragment(), SurfaceHolder.Callback {
    lateinit var binding: FragmentJanusBinding
    private lateinit var mPlayer: IjkMediaPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.i("============onViewCreated===========")
        binding.jumpVideoRoom.setOnClickListener {
            mNavController.navigate(JanusFragmentDirections.actionJanusFragmentToVideoRoomFragment())
        }

        binding.jumpVideoCall.setOnClickListener {
            mNavController.navigate(JanusFragmentDirections.actionJanusFragmentToVideoCallFragment())
        }
        binding.ijkSurfaceView.holder.addCallback(this)
        initPlayer()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_janus
    }

    override fun bindingSetViewModels() {
        binding = viewDataBinding as FragmentJanusBinding
    }

    private fun initPlayer() {
        mPlayer = IjkMediaPlayer()
        try {
            val path = Environment.getExternalStorageDirectory().path + "/Movies/299495755-1-80.flv"
            mPlayer.dataSource = path
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mPlayer.isLooping = true
        mPlayer.prepareAsync()
        mPlayer.start()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        //将所播放的视频图像输出到指定的SurfaceView组件
        mPlayer.setDisplay(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        mPlayer.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    override fun onPause() {
        super.onPause()
        mPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer.release()
    }
}