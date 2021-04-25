/**
 * Created with JackHou
 * Date: 2021/4/9
 * Time: 16:12
 * Description:屏幕的一个帮助类
 * 提供对px,sp,dip等等操作
 * 提供屏幕尺寸
 */

package com.qtimes.jetpackdemokotlin.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.provider.Settings
import android.view.View
import android.widget.ListView


object ScreenUtil {

    /**
     * 显示屏幕信息
     */
    fun displayMetrics(context: Context) {
        val dm = context.resources.displayMetrics
        val appHeight = dm.heightPixels
        val appWidth = dm.widthPixels
        val density = dm.density // 屏幕密度（0.75 / 1.0 / 1.5）
        val densityDpi = dm.densityDpi // 屏幕密度DPI（120 / 160 / 240）
        LogUtil.d("width=$appWidth, height=$appHeight, dendity=$density, densitydpi=$densityDpi")
    }


    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue （DisplayMetrics类中属性density）
     * @return
     */
    fun px2dip(pxValue: Float): Int {
        val r = Resources.getSystem()
        val scale = r.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    fun dip2px(dipValue: Float): Int {
        val r = Resources.getSystem()
        val scale = r.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    fun px2sp(pxValue: Float): Int {
        val r = Resources.getSystem()
        val fontScale = r.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    fun sp2px(spValue: Float): Int {
        val r = Resources.getSystem()
        val scale = r.displayMetrics.scaledDensity
        return (spValue * scale + 0.5f).toInt()
    }

    /**
     * 获取一个view的宽度
     *
     * @param view
     * @return
     */
    fun getMeasureWidth(view: View): Int {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        return view.measuredWidth
    }

    /**
     * 获取view的高度
     *
     * @param view
     * @return
     */
    fun getMeasureHeight(view: View): Int {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        return view.measuredHeight
    }

    /**
     * 根据listview最大高度动态调整可完整显示的item个数
     */
    fun setListViewHeightBasedOnMaxHeght(listView: ListView, maxHeight: Int) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        var itemCount = 0
        for (i in listAdapter.count - 1 downTo 0) {
            itemCount++
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0)
            // 使列表长度刚好可以完整展示所有item，且不超过高度参数maxHeight
            totalHeight += if (totalHeight + listItem.measuredHeight > maxHeight) {
                break
            } else {
                listItem.measuredHeight
            }
        }

        // 高度有溢出，需要滑动时，才动态调整高度
        if (itemCount < listAdapter.count) {
            val params = listView.layoutParams
            params.height = totalHeight + listView.dividerHeight * (itemCount - 1)
            listView.layoutParams = params
        }
    }

    /**
     * 获得当前系统的亮度值： 0~255
     */
    fun getSysScreenBrightness(context: Context): Int {
        var screenBrightness = 255
        try {
            screenBrightness = Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return screenBrightness
    }

    /**
     * 设置当前系统的亮度值:0~255
     */
    fun setSysScreenBrightness(context: Context, brightness: Int) {
        try {
            val resolver = context.contentResolver
            val uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS)
            Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
            resolver.notifyChange(uri, null) // 实时通知改变
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取手机屏幕亮度模式
     */
    fun getScreenBrightnessMode(context: Context): Int {
        var screenMode = 0
        try {
            screenMode = Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE
            )
        } catch (localException: Exception) {
        }
        return screenMode
    }

    /**
     * 判断屏幕亮度是否为自动模式
     *
     * @param context
     * @return
     */
    fun isAutoBrightnessMode(context: Context): Boolean {
        return getScreenBrightnessMode(context) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
    }

    /**
     * 停止屏幕亮度为自动模式
     *
     * @param context
     */
    fun stopAutoBrightness(context: Context) {
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue fontScale（DisplayMetrics类中属性scaledDensity）
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * px2sp
     *
     * @param context
     * @param pxValue
     * @return
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val scaled = context.resources.displayMetrics.scaledDensity
        return (pxValue / scaled + 0.5f).toInt()
    }

    fun getHeightInPx(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun getWidthInPx(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 判断是否为横屏
     *
     * @param context
     * @return
     */
    fun isPortrait(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    /**
     * 判断是否为横屏
     *
     * @param context
     * @return
     */
    fun isLandscape(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}