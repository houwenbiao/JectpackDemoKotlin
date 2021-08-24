/**
 * Created with JackHou
 * Date: 2021/8/24
 * Time: 17:13
 * Description:开机启动
 */

package com.qtimes.jetpackdemokotlin.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.qtimes.jetpackdemokotlin.ui.activity.MainActivity

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
    }
}
