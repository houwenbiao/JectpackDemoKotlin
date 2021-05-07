/**
 * Created with JackHou
 * Date: 2021/5/7
 * Time: 14:07
 * Description:门列表界面viewmodel
 */

package com.qtimes.jetpackdemokotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import com.qtimes.jetpackdemokotlin.model.DoorInfo
import com.qtimes.jetpackdemokotlin.repository.DeviceRepository
import com.qtimes.jetpackdemokotlin.utils.ESAMUtil
import com.qtimes.jetpackdemokotlin.viewmodel.base.BaseViewModel


class DoorListViewModel : BaseViewModel() {

    private val deviceRepository = DeviceRepository(this)
    val doors = MutableLiveData<MutableList<DoorInfo>>()//门列表
    val checkDoor = MutableLiveData<DoorInfo>()//当前选中的门类型

    /**
     * 获取门列表
     */
    fun getDoors() {
        deviceRepository.getDoors(ESAMUtil.getDeviceName()) {
            onSuccess { doorList ->
                doors.postValue(doorList)
            }
        }
    }
}