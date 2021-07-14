/**
 * Created with JackHou
 * Date: 2021/7/13
 * Time: 16:30
 * Description:工程施工程序界面的ViewModel
 */

package com.qtimes.jetpackdemokotlin.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.common.MainApplication
import com.qtimes.jetpackdemokotlin.model.DeviceMap
import com.qtimes.jetpackdemokotlin.repository.DeviceMapRepository
import com.qtimes.jetpackdemokotlin.room.AppDatabase
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import com.qtimes.jetpackdemokotlin.viewmodel.base.BaseViewModel
import java.security.acl.Owner

class ConstructionViewModel : BaseViewModel() {
    private val deviceMapRepository = DeviceMapRepository(this, AppDatabase.getInstance())
    lateinit var owner: LifecycleOwner

    val barCodeValue = MutableLiveData("")//条形码值
    val qrCodeValue = MutableLiveData("")//二维码值


    suspend fun addDeviceMap(): Long {
        if (barCodeValue.value.isNullOrEmpty()) {
            showToast(MainApplication.context.getString(R.string.illegable_code))
            return -1
        }
        if (qrCodeValue.value.isNullOrEmpty()) {
            showToast(MainApplication.context.getString(R.string.illegable_code))
            return -1
        }

        if (barCodeValue.value.equals(qrCodeValue.value)) {
            showToast(MainApplication.context.getString(R.string.illegable_code))
            return -1
        }

        val dMap = deviceMapRepository.findDeviceMap(barCodeValue.value!!, qrCodeValue.value!!)
        if (dMap != null) {
            showToast(MainApplication.context.getString(R.string.add_code_failed))
            return -1
        }

        return withIO {
            val deviceMap = DeviceMap(barCodeValue.value!!, qrCodeValue.value!!)
            deviceMapRepository.addDeviceMap(deviceMap)
        }
    }

    fun findDeviceMap(param: String?) {
        val deviceMap = deviceMapRepository.findDeviceMap(param)
        if (deviceMap != null) showToast(MainApplication.context.getString(R.string.code_exist))
    }
}