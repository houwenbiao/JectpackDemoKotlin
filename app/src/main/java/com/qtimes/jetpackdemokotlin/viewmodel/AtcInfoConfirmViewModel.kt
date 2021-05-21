/**
 * Created with JackHou
 * Date: 2021/5/14
 * Time: 9:26
 * Description:激活确认界面ViewModel
 */

package com.qtimes.jetpackdemokotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import com.qtimes.jetpackdemokotlin.model.CameraAngle
import com.qtimes.jetpackdemokotlin.model.DoorInfo
import com.qtimes.jetpackdemokotlin.viewmodel.base.BaseViewModel

class AtcInfoConfirmViewModel : BaseViewModel() {
    var doorInfo = MutableLiveData<DoorInfo>()//当前门类型
    var cameraAngle = MutableLiveData<CameraAngle>()//摄像头角度
}