/**
 * Created with JackHou
 * Date: 2021/8/23
 * Time: 16:43
 * Description:
 */

package com.qtimes.jetpackdemokotlin.audio

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import java.io.File
import java.io.IOException

class Player constructor(private val mContext: Context) {
    private var fd: AssetFileDescriptor? = null
    private var mPlayer: MediaPlayer? = null

    /**
     * 重头开始播放
     */
    fun restart() {
        if (mPlayer == null) {
            mPlayer = MediaPlayer()
        }
        mPlayer!!.reset()
        LogUtil.i("player reset")
        fd = mContext.resources.openRawResourceFd(R.raw.call_coming)
        try {
            mPlayer!!.setDataSource(fd!!.fileDescriptor, fd!!.startOffset, fd!!.length)
            fd?.close()
            mPlayer!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (mPlayer!!.isPlaying) {
            return
        }
        mPlayer!!.start()
    }

    fun play() {
        if (mPlayer == null) {
            mPlayer = MediaPlayer()
        }
        if (mPlayer!!.isPlaying) {
            return
        }
        mPlayer!!.reset()
        fd =
            mContext.resources.openRawResourceFd(R.raw.call_coming)
        try {
            mPlayer!!.setDataSource(fd!!.fileDescriptor, fd!!.startOffset, fd!!.length)
            fd?.close()
            mPlayer!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mPlayer!!.start()
    }

    fun play(path: String?, mediaPlayer: MediaPlayer?) {
        if (mediaPlayer != null) {
            mPlayer = mediaPlayer
        }
        if (mPlayer == null) {
            mPlayer = MediaPlayer()
        }
        mPlayer!!.reset()
        val uri = Uri.fromFile(File(path))
        try {
            mPlayer!!.setDataSource(mContext, uri)
            mPlayer!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (mPlayer!!.isPlaying) {
            return
        }
        mPlayer!!.start()
    }

    fun play(source: Int) {
        if (mPlayer == null) {
            mPlayer = MediaPlayer()
        }
        mPlayer?.reset()
        mPlayer?.isLooping = true
        fd = mContext.resources.openRawResourceFd(source)
        try {
            mPlayer!!.setDataSource(fd!!.fileDescriptor, fd!!.startOffset, fd!!.length)
            fd?.close()
            mPlayer!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (mPlayer!!.isPlaying) {
            return
        }
        mPlayer!!.start()
    }

    fun start() {
        if (mPlayer != null) {
            mPlayer!!.start()
        }
    }

    fun pause() {
        if (mPlayer != null) {
            mPlayer!!.pause()
        }
    }

    fun stop() {
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer!!.stop()
        }
    }

    fun release() {
        if (mPlayer != null) {
            mPlayer!!.release()
        }
    }
}