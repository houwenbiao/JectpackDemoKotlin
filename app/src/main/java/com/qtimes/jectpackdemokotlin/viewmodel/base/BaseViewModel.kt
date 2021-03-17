/**
 * Created with JackHou
 * Date: 2021/3/15
 * Time: 18:27
 * Description:ViewModel基类,实现了IBaseViewModel接口,创建可观察变量baseActionEvent
 * UI层通过观察baseActionEvent来调用相应的方法
 */

package com.qtimes.jectpackdemokotlin.viewmodel.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController


open class BaseViewModel : ViewModel(), IBaseViewModel {

    val baseActionEvent = MutableLiveData<ViewModelEvent>()
    var mNavController: NavController? = null
    override fun showLoading(msg: String) {
        val event = ViewModelEvent(ViewModelEvent.SHOW_LOADING_DIALOG)
        event.message = msg
        baseActionEvent.postValue(event)
    }

    override fun dismissLoading() {
        val event = ViewModelEvent(ViewModelEvent.DISMISS_LOADING_DIALOG)
        baseActionEvent.postValue(event)
    }

    override fun showToast(msg: String) {
        val event = ViewModelEvent(ViewModelEvent.SHOW_TOAST)
        event.message = msg
        baseActionEvent.postValue(event)
    }

    override fun finishView() {
        val event = ViewModelEvent(ViewModelEvent.FINISH)
        baseActionEvent.postValue(event)
    }

    override fun pop() {
        val event = ViewModelEvent(ViewModelEvent.POP)
        baseActionEvent.postValue(event)
    }

    fun setNavController(navController: NavController) {
        this.mNavController = navController
    }
}