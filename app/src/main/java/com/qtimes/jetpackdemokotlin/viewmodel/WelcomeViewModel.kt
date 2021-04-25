/**
 * Created with JackHou
 * Date: 2021/4/2
 * Time: 14:14
 * Description:Welcome界面的ViewModel
 */

package com.qtimes.jetpackdemokotlin.viewmodel

import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qtimes.jetpackdemokotlin.common.Const
import com.qtimes.jetpackdemokotlin.common.MainApplication
import com.qtimes.jetpackdemokotlin.model.User
import com.qtimes.jetpackdemokotlin.model.UserState
import com.qtimes.jetpackdemokotlin.repository.UserRepository
import com.qtimes.jetpackdemokotlin.room.AppDatabase
import com.qtimes.jetpackdemokotlin.ui.activity.MainActivity
import com.qtimes.jetpackdemokotlin.utils.DataStoreUtil
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import com.qtimes.jetpackdemokotlin.viewmodel.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class WelcomeViewModel : BaseViewModel() {

    companion object {
        const val WELCOME_SHOW_TIME = 5
    }

    private val userRepository = UserRepository(this, AppDatabase.getInstance())
    lateinit var owner: LifecycleOwner
    val timerCount = MutableLiveData(WELCOME_SHOW_TIME)
    var userName = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val password = MutableLiveData<String>()


    init {
        LogUtil.d("WelcomeViewModel init")
        viewModelScope.launch {
            repeat(WELCOME_SHOW_TIME) {
                delay(1000)
                timerCount.postValue(WELCOME_SHOW_TIME - 1 - it)
            }
        }
    }

    suspend fun registerUser(): Long {
        if (userName.value.isNullOrEmpty()) {
            showToast("请输入合法用户名")
            return -1
        }

        phone.value?.let { LogUtil.d(it) }

        if (phone.value?.length != 11) {
            showToast("手机号输入有误")
            return -1
        }

        if (password.value.isNullOrEmpty() || password.value!!.length < 10) {
            showToast("密码请输入十位以上字符串")
            return -1
        }

        return withIO {
            userRepository.resisterUser(
                User(
                    name = userName.value!!,
                    password = password.value!!,
                    phone = phone.value,
                    state = UserState.OFFLINE.name
                )
            )
        }
    }


    fun login() {

        if (userName.value.isNullOrEmpty()) {
            showToast("用户名输入错误")
            return
        }
        userName.value?.let {
            LogUtil.d("login: $it")
            val liveDataUser = userRepository.findUserByAccountOrPhone(it)
            //此处直接打印user.value = null,需要使用observe的方式去监管获取数据
            liveDataUser.observe(owner) { it1 ->
                if (it1 == null) {
                    showToast("用户尚未注册")
                }
                it1?.let { user ->
                    LogUtil.d(user)
                    if (user.password == password.value) {
                        showToast("登录成功")
                        DataStoreUtil.setString(
                            Const.KEY_USER_STATE,
                            UserState.ONLINE.name,
                            viewModelScope
                        )
                        val intentMain = Intent(MainApplication.context, MainActivity::class.java)
                        MainApplication.context.startActivity(intentMain)
                    } else {
                        showToast("密码错误")
                    }
                }
            }
        }
    }
}