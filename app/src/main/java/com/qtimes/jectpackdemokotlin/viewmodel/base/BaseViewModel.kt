/**
 * Created with JackHou
 * Date: 2021/3/15
 * Time: 18:27
 * Description:ViewModel基类,实现了IBaseViewModel接口,创建可观察变量baseActionEvent
 * UI层通过观察baseActionEvent来调用相应的方法
 */

package com.qtimes.jectpackdemokotlin.viewmodel.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qtimes.jectpackdemokotlin.net.base.*
import kotlinx.coroutines.CoroutineScope


open class BaseViewModel : ViewModel(), IViewModelActionEvent {

    override val lifecycleSupportedScope: CoroutineScope
        get() = viewModelScope

    override val showLoadingEventLD = MutableLiveData<ShowLoadingEvent>()

    override val dismissLoadingEventLD = MutableLiveData<DismissLoadingEvent>()

    override val showToastEventLD = MutableLiveData<ShowToastEvent>()

    override val finishViewEventLD = MutableLiveData<FinishViewEvent>()

}

open class BaseAndroidViewModel(application: Application) : AndroidViewModel(application),
    IViewModelActionEvent {

    override val lifecycleSupportedScope: CoroutineScope
        get() = viewModelScope

    override val showLoadingEventLD = MutableLiveData<ShowLoadingEvent>()

    override val dismissLoadingEventLD = MutableLiveData<DismissLoadingEvent>()

    override val showToastEventLD = MutableLiveData<ShowToastEvent>()

    override val finishViewEventLD = MutableLiveData<FinishViewEvent>()
}