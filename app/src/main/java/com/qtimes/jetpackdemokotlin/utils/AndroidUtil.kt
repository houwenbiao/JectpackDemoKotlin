package com.qtimes.jetpackdemokotlin.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import android.provider.Settings
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


object AndroidUtil {
    const val VERSION_NAME = "versionName"
    const val VERSION_CODE = "versionCode"


    /**
     * 读取硬件版本对应的电压
     * 返回的是电压值
     *
     * @return string
     */
    private fun getPCBVoltage(): String {
        val pathname =
            "/sys/bus/iio/devices/iio:device0/in_voltage5_raw" // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        var str: String?
        val pcbVoltageStr = StringBuilder()
        var reader: FileReader? = null
        var br: BufferedReader? = null
        try {
            reader = FileReader(pathname)
            br = BufferedReader(reader)
            while (br.readLine().also { str = it } != null) {
                pcbVoltageStr.append(str)
            }
            reader.close()
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                br?.close()
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return pcbVoltageStr.toString()
    }

    /**
     * 获取硬件版本号
     *
     * @return version
     */
    fun getPCBVersion(): String {
        val v = getPCBVoltage()
        val voltage = if (v.isNotEmpty()) v.toInt() else 1000
        if (voltage >= 900) {
            return "1.0"
        } else if (voltage > 400) {
            return "2.0"
        } else if (voltage > 200) {
            return "3.0"
        } else if (voltage > 100) {
            return "4.0"
        }
        return voltage.toString()
    }

    /**
     * 获取应用程序版本名称
     *
     * @param context
     * @return
     */
    fun getAppVersionName(context: Context): String {
        try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            return info.versionName
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }


    /**
     * 获取应用程序版本号
     *
     * @param context
     * @return
     */
    fun getAppVersion(context: Context): Int {
        try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            return info.versionCode
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }
        return 1
    }

    /**
     * 获取android id
     * @param context ctx
     * @return
     */
    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}