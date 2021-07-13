/**
 * Created with JackHou
 * Date: 2021/7/13
 * Time: 16:30
 * Description:
 */

package com.qtimes.jetpackdemokotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import com.qtimes.jetpackdemokotlin.repository.DeviceMapRepository
import com.qtimes.jetpackdemokotlin.room.AppDatabase
import com.qtimes.jetpackdemokotlin.viewmodel.base.BaseViewModel

/**
 * Author: JackHou
 * Date: 2021/7/13.
 */
class ConstructionViewModel : BaseViewModel() {
    private val deviceMapRepository = DeviceMapRepository(this, AppDatabase.getInstance())

    val barCodeValue = MutableLiveData<String>("")//条形码值
    val qrCodeValue = MutableLiveData("")//二维码值
}