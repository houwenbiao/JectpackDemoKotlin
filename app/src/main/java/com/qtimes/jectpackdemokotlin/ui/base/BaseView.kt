/**
 * Created with JackHou
 * Date: 2021/3/16
 * Time: 9:59
 * Description:
 */

package com.qtimes.jectpackdemokotlin.ui.base

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.qtimes.jectpackdemokotlin.viewmodel.base.IBaseViewModelEventObserver

abstract class BaseFragment : Fragment(), IBaseViewModelEventObserver {
    lateinit var progressDialog: ProgressDialog
    lateinit var viewDataBinding: ViewDataBinding
    lateinit var mNavController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        viewDataBinding.lifecycleOwner = this//建立双向绑定的关键
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        progressDialog = ProgressDialog(getCtx())
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        observeViewModelEvent()
        bindingSetViewModels()
    }

    abstract fun getLayoutId(): Int

    //ViewDataBinding set model
    protected abstract fun bindingSetViewModels()

    override fun getLifecycleOwner(): LifecycleOwner {
        return this
    }

    override fun getCtx(): Context {
        return this.requireContext()
    }

    override fun showLoading(msg: String) {
        progressDialog.setTitle(msg)
        progressDialog.show()
    }

    override fun dismissLoading() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    override fun finishView() {
        activity?.finish()
    }

    override fun pop() {
        mNavController.popBackStack()
    }
}

abstract class BaseActivity : AppCompatActivity(), IBaseViewModelEventObserver {
    lateinit var progressDialog: ProgressDialog
    lateinit var viewDataBinding: ViewDataBinding
    private var mNavController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mNavController = getNavController()
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        observeViewModelEvent()
    }


    abstract fun getLayoutId(): Int

    open fun getNavController(): NavController? {
        return null
    }

    //ViewDataBinding set model
    protected abstract fun bindingSetViewModels()

    override fun getLifecycleOwner(): LifecycleOwner {
        return this
    }

    override fun getCtx(): Context {
        return this
    }

    override fun showLoading(msg: String) {
        progressDialog.setTitle(msg)
        progressDialog.show()
    }

    override fun dismissLoading() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    override fun finishView() {
        finish()
    }

    override fun pop() {
        mNavController?.popBackStack()
    }
}