/**
 * Created with JackHou
 * Date: 2021/3/15
 * Time: 18:21
 * Description:
 */

package com.qtimes.jectpackdemokotlin.viewmodel.base

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import java.util.function.Consumer

/*VM层接口*/
interface IBaseViewModel {

    fun showLoading() {
        showLoading("")
    }

    fun showLoading(msg: String)

    fun dismissLoading()

    fun showToast(msg: String)

    fun finishView()

    fun pop()
}


//View层实现这个监听来接收VM层的事件
interface IBaseViewModelEventObserver : IBaseViewModel {

    fun initViewModel(): BaseViewModel? {
        return null
    }

    fun initViewModels(): MutableList<BaseViewModel>? {
        return null
    }

    fun observeViewModelEvent() {
        var viewModels: MutableList<BaseViewModel>? = null
        val viewModelList = initViewModels()
        if (viewModelList.isNullOrEmpty()) {
            val viewModel = initViewModel()
            viewModel?.let { viewModels = mutableListOf(it) }
        } else {
            viewModels = viewModelList
        }

        viewModels?.forEach(Consumer { baseVM ->
            baseVM.baseActionEvent.observe(getLifecycleOwner(), Observer {
                when (it.action) {
                    ViewModelEvent.SHOW_LOADING_DIALOG -> showLoading(it.message)
                    ViewModelEvent.DISMISS_LOADING_DIALOG -> dismissLoading()
                    ViewModelEvent.SHOW_TOAST -> showToast(it.message)
                    ViewModelEvent.FINISH -> finishView()
                    ViewModelEvent.POP -> pop()
                }
            })
        })
    }

    fun getLifecycleOwner(): LifecycleOwner

    fun getCtx(): Context

    override fun showToast(msg: String) {
        Toast.makeText(getCtx(), msg, Toast.LENGTH_LONG).show()
    }
}