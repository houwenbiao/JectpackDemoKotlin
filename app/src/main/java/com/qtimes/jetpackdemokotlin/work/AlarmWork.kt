/**
 * Created with JackHou
 * Date: 2021/4/13
 * Time: 11:05
 * Description:定时后台任务测试
 */

package com.qtimes.jetpackdemokotlin.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.qtimes.jetpackdemokotlin.utils.LogUtil


class AlarmWork(ctx: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(ctx, workerParameters) {

    override suspend fun doWork(): Result {
        LogUtil.d("Alarm")
        return Result.success()
    }
}