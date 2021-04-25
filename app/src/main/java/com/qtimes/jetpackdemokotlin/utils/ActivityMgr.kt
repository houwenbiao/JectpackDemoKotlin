package com.qtimes.jetpackdemokotlin.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.qtimes.jetpackdemokotlin.common.MainApplication
import java.util.*


class ActivityMgr {

    companion object {
        const val TAG = "ActivityManager"
        @SuppressLint("StaticFieldLeak")
        private var mCurActivity: Context? = null
        private var mCacheActivities: LinkedHashMap<Int?, Activity>? = null

        /**
         * activity栈size
         */
        fun getCacheSize(): Int {
            return mCacheActivities!!.size
        }

        fun getCurActivity(): Activity? {
            return mCurActivity as Activity?
        }

        /**
         * 通过调用对应activity的finish方法，将其从缓存列表中移除
         */
        fun removeActivity(activity: Class<*>?) {
            if (mCacheActivities == null || activity == null) {
                return
            }
            val activitys: MutableList<Activity?> = mutableListOf()
            for (entry: Map.Entry<Int?, Activity> in mCacheActivities!!.entries) {
                activitys.add(entry.value)
            }
            for (act: Activity? in activitys) {
                if (act!!.javaClass.name == activity.name) {
                    if (!act.isFinishing) {
                        mCacheActivities!!.remove(activity.hashCode())
                        act.finish()
                    }
                }
            }
        }

        /**
         * 添加当前Activity到ActivityManager
         *
         * @param activity
         */
        fun addActivity(activity: Activity) {
            mCurActivity = activity
            if (mCacheActivities == null) {
                mCacheActivities = LinkedHashMap()
            }
            val hashCode = activity.hashCode()
            if (mCacheActivities!!.containsKey(hashCode)) {
                mCacheActivities!!.remove(hashCode)
            }
            mCacheActivities!![hashCode] = activity
            LogUtil.i(
                "addActivity.activity = "
                        + activity.javaClass.simpleName
                        + ", mCacheActivities.size() = " + mCacheActivities!!.size
            )
        }

        /**
         * 在ActivityManager中回收指定的Activity
         *
         * @param activity
         */
        fun destroyActivity(activity: Activity) {
            if (mCacheActivities != null) {
                mCacheActivities!!.remove(activity.hashCode())
                if (mCurActivity === activity) {
                    mCurActivity = null
                }
                LogUtil.i(
                    ("destroyActivity.activity = "
                            + activity.javaClass.simpleName
                            + ", mCacheActivities.size() = " + mCacheActivities!!.size)
                )
            }
        }

        /**
         * 结束所有Activity
         *
         * @param isIgnoreCurrentActivity 是否忽略当前Activity
         * @return
         */
        fun finishAllActivity(isIgnoreCurrentActivity: Boolean): Int {
            var finishCount = 0
            LogUtil.i(
                ("finishAllActivity.mCacheActivities.size() = "
                        + (if (mCacheActivities == null) 0 else mCacheActivities!!.size))
            )
            if (mCacheActivities != null && !mCacheActivities!!.isEmpty()) {
                val activitys: MutableList<Activity> = ArrayList()
                for (entry: Map.Entry<Int?, Activity> in mCacheActivities!!.entries) {
                    activitys.add(entry.value)
                }
                for (activity: Activity in activitys) {
                    if ((!isIgnoreCurrentActivity
                                || (isIgnoreCurrentActivity && activity !== mCurActivity))
                    ) {
                        if (!activity.isFinishing) {
                            activity.finish()
                            finishCount++
                            LogUtil.i(
                                ("finishAllActivity.activity = "
                                        + activity.javaClass.simpleName
                                        + " finished")
                            )
                        }
                    } else {
                        mCacheActivities!!.remove(activity.hashCode())
                    }
                }
            }
            mCurActivity = null
            return finishCount
        }

        fun findActivity(name: String): Boolean {
            var find = false
            if (TextUtils.isEmpty(name)) {
                return find
            }
            if (mCacheActivities != null && !mCacheActivities!!.isEmpty()) {
                val activitys: MutableList<Activity> = ArrayList()
                for (entry: Map.Entry<Int?, Activity> in mCacheActivities!!.entries) {
                    activitys.add(entry.value)
                }
                for (activity: Activity in activitys) {
                    val activityName = activity.javaClass.simpleName
                    if ((name == activityName)) {
                        find = true
                        break
                    }
                }
            }
            return find
        }

        fun isTopActivity(
            targetAppPackageName: String,
            targetAppActivityName: String
        ): Boolean {
            val TAG = "Check_Top_Activity"
            val mActivityManager =
                MainApplication.context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mTasks = mActivityManager.getRunningTasks(1) //5.0
            if (mTasks[0] != null) {
                val topActivity = mTasks[0]!!.topActivity
                if (((topActivity != null) && !TextUtils.isEmpty(topActivity.packageName) &&
                            !TextUtils.isEmpty(topActivity.className))
                ) {
                    Log.w(TAG, "topActivity.getPackageName() -> " + topActivity.packageName)
                    Log.w(TAG, "topActivity.getClassName() -> " + topActivity.className)
                    if ((topActivity.packageName == targetAppPackageName) && (topActivity.className == targetAppActivityName)) {
                        Log.d(TAG, "target Activity is in top !")
                        return true
                    } else {
                        Log.w(TAG, "target Activity is not in top.")
                        return false
                    }
                } else {
                    Log.w(TAG, "topActivity is null or PackageName is null or ClassName is null.")
                    return false
                }
            } else {
                Log.w(TAG, "mTasks.get(0) == null ")
                return false
            }
        }

        fun isTopActivity(packageName: String): Boolean {
            val am =
                MainApplication.context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val list = am.runningAppProcesses
            if (list.size == 0) {
                return false
            }
            for (process: RunningAppProcessInfo in list) {
                if (process.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && (process.processName == packageName)) {
                    return true
                }
            }
            return false
        }

        fun isTopActivity(activity: Activity): Boolean {
            var isTop = false
            val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val cn = am.getRunningTasks(1)[0].topActivity
            if (cn!!.className.contains(activity.javaClass.name)) {
                isTop = true
            }
            return isTop
        }

        fun isActivityRunning(className: String): Boolean {
            val am =
                MainApplication.context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info = am.getRunningTasks(1)
            if (info != null && info.size > 0) {
                val component = info[0].topActivity
                LogUtil.i("topActivityName: " + component!!.className + " className:" + className)
                if ((className == component.className)) {
                    return true
                }
            }
            return false
        }

        /**
         * 指定包名的activity是否正在运行
         *
         * @param packageName 包名
         * @return boolean
         */
        fun isPackageActivityRunning(packageName: String): Boolean {
            val am =
                MainApplication.context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info = am.getRunningTasks(1)
            if (info != null && info.size > 0) {
                val component = info[0].topActivity
                LogUtil.i("TopActivityName: " + component!!.className + ", packageName:" + packageName)
                return component.className.contains(packageName)
            }
            return false
        }
    }
}