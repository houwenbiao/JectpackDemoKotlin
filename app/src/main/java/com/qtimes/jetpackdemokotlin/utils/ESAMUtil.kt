package com.qtimes.jetpackdemokotlin.utils

import cc.qtimes.esam.ESAM
import com.qtimes.jetpackdemokotlin.common.Const


object ESAMUtil {
    fun getDeviceInfo(): Map<String, String> {
        val mAcquireCiphers: Map<String, String> = ESAM.getInstance().acquireCiphers()
        LogUtil.i("getDeviceInfo: $mAcquireCiphers")
        return mAcquireCiphers
    }

    fun getDeviceName(): String {
        val mAcquireCiphers: Map<String, String> = ESAM.getInstance().acquireCiphers()
        LogUtil.i("getDeviceName: $mAcquireCiphers")
        return mAcquireCiphers[Const.KEY_DEVICE_NAME] as String
    }

    fun getProductKey(): String {
        val mAcquireCiphers: Map<String, String> = ESAM.getInstance().acquireCiphers()
        LogUtil.i("getProductKey: $mAcquireCiphers")
        return mAcquireCiphers[Const.KEY_PRODUCT_KEY] as String
    }

    fun getDeviceSecret(): String {
        val mAcquireCiphers: Map<String, String> = ESAM.getInstance().acquireCiphers()
        LogUtil.i("getDeviceSecret: $mAcquireCiphers")
        return mAcquireCiphers[Const.KEY_DEVICE_SECRET] as String
    }
}