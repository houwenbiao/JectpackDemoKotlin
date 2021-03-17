/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 15:16
 * Description:
 */

package com.qtimes.jectpackdemokotlin.common

import android.content.Context
import android.widget.Toast


class ToastHolder {
    companion object {
        fun showToast(context: Context = JApplication.context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}