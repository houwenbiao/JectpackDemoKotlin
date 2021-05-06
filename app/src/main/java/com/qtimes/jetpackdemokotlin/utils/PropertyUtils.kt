package com.qtimes.jetpackdemokotlin.utils

import java.lang.reflect.Method


object PropertyUtils {
    @Volatile
    private var set: Method? = null

    @Volatile
    private var get: Method? = null

    operator fun set(prop: String, value: String) {
        try {
            if (null == set) {
                synchronized(PropertyUtils::class.java) {
                    if (null == set) {
                        val cls =
                            Class.forName("android.os.SystemProperties")
                        set = cls.getDeclaredMethod(
                            "set", *arrayOf<Class<*>>(
                                String::class.java,
                                String::class.java
                            )
                        )
                    }
                }
            }
            set!!.invoke(null, *arrayOf<Any>(prop, value))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    operator fun get(prop: String?, defaultvalue: String): String {
        var value = defaultvalue
        try {
            if (null == get) {
                synchronized(PropertyUtils::class.java) {
                    if (null == get) {
                        val cls =
                            Class.forName("android.os.SystemProperties")
                        get = cls.getDeclaredMethod(
                            "get", *arrayOf<Class<*>>(
                                String::class.java,
                                String::class.java
                            )
                        )
                    }
                }
            }
            value = get!!.invoke(null, *arrayOf<Any?>(prop, defaultvalue)) as String
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return value
    }

    /**
     * 重启NPU
     */
    fun restartNpu() {
        set("ctl.stop", "npu_transfer_proxy")
        set("ctl.start", "npu_transfer_proxy")
    }

    /**
     * 重启camera service
     */
    fun restartCameraService() {
        set("ctl.stop", "cameraserver")
        set("ctl.start", "cameraserver")
    }

    /**
     * 获取系统版本名称
     *
     * @return
     */
    fun getSystemVersionName(): String {
        return get("ro.product.version", "")
    }

    /**
     * 获取系统版本序列号
     *
     * @return
     */
    fun getSystemSN(): String? {
        return get("ro.serialno", "")
    }

    /**
     * open wireless adb
     */
    fun openAdb() {
        set("persist.internet.adb.enable", "1")
    }

    /**
     * close wireless adb
     */
    fun closeAdb() {
        set("persist.internet.adb.enable", "0")
    }

}