/**
 * Created with JackHou
 * Date: 2021/3/23
 * Time: 16:04
 * Description:用于定义 View 和  ViewModel 均需要实现的一些 UI 层行为
 */

package com.qtimes.jetpackdemokotlin.net.base

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.Job

interface IUIActionEvent : ICoroutineEvent {
    fun showLoading(job: Job?)
    fun dismissLoading()
    fun showToast(msg: String)
    fun finishView()
}


interface IViewModelActionEvent : IUIActionEvent {
    val showLoadingEventLD: MutableLiveData<ShowLoadingEvent>

    val dismissLoadingEventLD: MutableLiveData<DismissLoadingEvent>

    val showToastEventLD: MutableLiveData<ShowToastEvent>

    val finishViewEventLD: MutableLiveData<FinishViewEvent>

    override fun showLoading(job: Job?) {
        showLoadingEventLD.value = ShowLoadingEvent(job)
    }

    override fun dismissLoading() {
        dismissLoadingEventLD.value = DismissLoadingEvent
    }

    override fun showToast(msg: String) {
        showToastEventLD.value = ShowToastEvent(msg)
    }

    override fun finishView() {
        finishViewEventLD.value = FinishViewEvent
    }
}

interface IUIActionEventObserver : IUIActionEvent {

    val mContext: Context?

    val mLifecycleOwner: LifecycleOwner

    fun <VM> getViewModel(
        clazz: Class<VM>,
        factory: ViewModelProvider.Factory? = null,
        initializer: (VM.(lifecycleOwner: LifecycleOwner) -> Unit)? = null
    ): Lazy<VM> where VM : ViewModel, VM : IViewModelActionEvent {
        return lazy {
            getViewModelFast(clazz, factory, initializer)
        }
    }

    fun <VM> getViewModelFast(
        clazz: Class<VM>,
        factory: ViewModelProvider.Factory? = null,
        initializer: (VM.(lifecycleOwner: LifecycleOwner) -> Unit)? = null
    ): VM where VM : ViewModel, VM : IViewModelActionEvent {
        return when (val localValue = mLifecycleOwner) {
            is ViewModelStoreOwner -> {
                if (factory == null) {
                    ViewModelProvider(localValue).get(clazz)
                } else {
                    ViewModelProvider(localValue, factory).get(clazz)
                }
            }
            else -> {
                factory?.create(clazz) ?: clazz.newInstance()
            }
        }.apply {
            generateActionEvent(this)
            initializer?.invoke(this, mLifecycleOwner)
        }
    }

    fun <VM> generateActionEvent(viewModel: VM) where VM : ViewModel, VM : IViewModelActionEvent {
        viewModel.showLoadingEventLD.observe(mLifecycleOwner, Observer {
            showLoading(it.job)
        })
        viewModel.dismissLoadingEventLD.observe(mLifecycleOwner, Observer {
            dismissLoading()
        })
        viewModel.showToastEventLD.observe(mLifecycleOwner, Observer {
            if (it.message.isNotBlank()) {
                showToast(it.message)
            }
        })
        viewModel.finishViewEventLD.observe(mLifecycleOwner, Observer {
            finishView()
        })
    }

}


