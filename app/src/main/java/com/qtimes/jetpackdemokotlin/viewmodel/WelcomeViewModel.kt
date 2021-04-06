/**
 * Created with JackHou
 * Date: 2021/4/2
 * Time: 14:14
 * Description:Welcome界面的ViewModel
 */

package com.qtimes.jetpackdemokotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import com.qtimes.jetpackdemokotlin.viewmodel.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class WelcomeViewModel : BaseViewModel() {

    companion object {
        const val WELCOME_SHOW_TIME = 5
    }

    val timerCount = MutableLiveData(WELCOME_SHOW_TIME)
    var userName = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    init {
        globalScope.launch {
            repeat(WELCOME_SHOW_TIME) {
                delay(1000)
                timerCount.postValue(WELCOME_SHOW_TIME - 1 - it)
            }
        }
    }
}