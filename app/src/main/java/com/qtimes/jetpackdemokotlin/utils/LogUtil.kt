/**
 * Created with JackHou
 * Date: 2021/3/16
 * Time: 17:59
 * Description:
 */

package com.qtimes.jetpackdemokotlin.utils

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log


object LogUtil {
    private const val tagPrefix = "LogUtil"
    private var debug = true
    private var INDEX = 5
    fun d(o: Any) {
        logger("d", o, INDEX)
    }

    fun e(o: Any) {
        logger("e", o, INDEX)
    }

    fun i(o: Any) {
        logger("i", o, INDEX)
    }

    fun w(o: Any) {
        logger("w", o, INDEX)
    }

    fun d(o: Any, i: Int) {
        logger("d", o, i)
    }

    fun e(o: Any, i: Int) {
        logger("e", o, i)
    }

    fun i(o: Any, i: Int) {
        logger("i", o, i)
    }

    fun w(o: Any, i: Int) {
        logger("w", o, i)
    }

    /**
     * @param type logger级别
     * @param o    logger内容
     */
    private fun logger(type: String, o: Any, index: Int) {
        if (!debug) {
            return
        }
        val msg = o.toString() + ""
        val tag = getTag(getCallerStackTraceElement(index))
        when (type) {
            "i" -> Log.i(tag, msg)
            "d" -> Log.d(tag, msg)
            "e" -> Log.e(tag, msg)
            "w" -> Log.w(tag, msg)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getTag(element: StackTraceElement): String {
        return try {
            var tag = "%s.%s(Line:%d)" // 占位符
            var callerClazzName = element.className // 获取到类名
            callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
            tag = String.format(
                tag,
                callerClazzName,
                element.methodName,
                element.lineNumber
            ) // 替换
            tag = if (TextUtils.isEmpty(tagPrefix)) tag else tagPrefix + ":" + tag
            tag
        } catch (e: Exception) {
            tagPrefix
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getTagThread(element: StackTraceElement): String {
        return try {
            var tag = "%s.%s(Line:%d, Thread:%s)" // 占位符
            var callerClazzName = element.className // 获取到类名
            val threadName = Thread.currentThread().name
            callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
            tag = String.format(
                tag,
                callerClazzName,
                element.methodName,
                element.lineNumber,
                threadName
            ) // 替换
            tag = if (TextUtils.isEmpty(tagPrefix)) tag else tagPrefix + ":" + tag
            tag
        } catch (e: Exception) {
            tagPrefix
        }
    }

    /**
     * 获取线程状态
     *
     * @return StackTraceElement
     */
    private fun getCallerStackTraceElement(index: Int): StackTraceElement {
        return Thread.currentThread().stackTrace[index]
    }
}