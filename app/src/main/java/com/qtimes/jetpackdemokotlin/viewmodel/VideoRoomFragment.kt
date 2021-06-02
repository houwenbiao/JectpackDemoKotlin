/**
 * Created with JackHou
 * Date: 2021/5/21
 * Time: 13:36
 * Description:VideoRoom viewmodel
 */

package com.qtimes.jetpackdemokotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import com.qtimes.jetpackdemokotlin.common.MainApplication
import com.qtimes.jetpackdemokotlin.utils.AndroidUtil
import com.qtimes.jetpackdemokotlin.viewmodel.base.BaseViewModel

class VideoRoomViewModel : BaseViewModel() {
    var userName = MutableLiveData<String>(AndroidUtil.getAndroidId(MainApplication.context))
}