/**
 * Created with JackHou
 * Date: 2021/4/26
 * Time: 17:29
 * Description:设备ViewModel
 */

package com.qtimes.jetpackdemokotlin.viewmodel

import android.graphics.Color
import androidx.datastore.dataStore
import androidx.lifecycle.MutableLiveData
import com.qtimes.jetpackdemokotlin.common.Const
import com.qtimes.jetpackdemokotlin.common.MainApplication
import com.qtimes.jetpackdemokotlin.model.AtcState
import com.qtimes.jetpackdemokotlin.model.AuthenticateBody
import com.qtimes.jetpackdemokotlin.repository.DeviceRepository
import com.qtimes.jetpackdemokotlin.utils.*
import com.qtimes.jetpackdemokotlin.viewmodel.base.BaseViewModel


class DeviceViewModel : BaseViewModel() {

    val deviceRepository = DeviceRepository(this)

    var atcState = MutableLiveData(AtcState.UNAUTHENTICATED)

    var txtAtcStatusColor = MutableLiveData(Color.RED)

    /**
     * 设备认证
     */
    fun atcDevice() {
        val deviceInfo: Map<String, String> = ESAMUtil.getDeviceInfo()
        val productKey = deviceInfo[Const.KEY_PRODUCT_KEY] ?: ""
        val deviceName = deviceInfo[Const.KEY_DEVICE_NAME] ?: ""
        val deviceSecret = deviceInfo[Const.KEY_DEVICE_SECRET] ?: ""

        val systemVersionName: String = PropertyUtils.getSystemVersionName() //系统版本
        val appVersionName: String =
            AndroidUtil.getAppVersionName(MainApplication.context) //主识别程序版本
        val aid = AndroidUtil.getAndroidId(MainApplication.context)
        val appVersionCode: Int = AndroidUtil.getAppVersion(MainApplication.context) //主识别程序版本号
        val pcbInfo: String = AndroidUtil.getPCBVersion()
        val modelInfo = ""
        val location = "上海市浦东新区"
        val userAgent = "OS/" + systemVersionName +
                ";WonlySmart/" + appVersionCode + "/" + appVersionName +
                ";DoorInfo/" + "" +
                ";Model/" + modelInfo +
                ";Location/" + location +
                ";PCB/" + pcbInfo

        deviceRepository.authenticateDevice(
            AuthenticateBody(
                productKey,
                deviceName,
                deviceSecret,
                "1234545656666666",
                aid,
                userAgent
            )
        ) {
            onStart {
                atcState.postValue(AtcState.AUTHENTICATING)
            }
            onSuccess {
                LogUtil.d(it)
                atcState.postValue(AtcState.AUTHENTICATED)
            }

            onFailed {
                atcState.postValue(AtcState.AUTHENTICATE_FAILED)
            }
        }
    }
}

