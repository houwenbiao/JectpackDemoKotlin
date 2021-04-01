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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.qtimes.jectpackdemokotlin.net.base.IUIActionEventObserver
import com.qtimes.jectpackdemokotlin.net.base.IViewModelActionEvent
import com.qtimes.jectpackdemokotlin.utils.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

abstract class BaseFragment : Fragment(), IUIActionEventObserver {
    lateinit var progressDialog: ProgressDialog
    lateinit var viewDataBinding: ViewDataBinding
    lateinit var mNavController: NavController

    override val lifecycleSupportedScope: CoroutineScope
        get() = lifecycleScope

    override val mContext: Context?
        get() = context

    override val lLifecycleOwner: LifecycleOwner
        get() = this

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
        progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        bindingSetViewModels()
    }

    abstract fun getLayoutId(): Int

    //ViewDataBinding set model
    protected abstract fun bindingSetViewModels()


    override fun showLoading(job: Job?) {
        LogUtil.d("BaseFragment------->showLoading")
        progressDialog.show()
    }

    override fun dismissLoading() {
        progressDialog.takeIf { it.isShowing }?.dismiss()
    }

    override fun showToast(msg: String) {
        if (msg.isNotBlank()) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun finishView() {

    }

}

abstract class BaseActivity : AppCompatActivity(), IUIActionEventObserver {
    lateinit var progressDialog: ProgressDialog
    lateinit var viewDataBinding: ViewDataBinding
    private var mNavController: NavController? = null

    override val lifecycleSupportedScope: CoroutineScope
        get() = lifecycleScope

    override val mContext: Context?
        get() = this

    override val lLifecycleOwner: LifecycleOwner
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mNavController = getNavController()
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
    }


    abstract fun getLayoutId(): Int

    open fun getNavController(): NavController? {
        return null
    }

    //ViewDataBinding set model
    protected abstract fun bindingSetViewModels()

    override fun showLoading(job: Job?) {
        LogUtil.d("BaseActivity------->showLoading")
        progressDialog.show()
    }

    override fun dismissLoading() {
        progressDialog.takeIf { it.isShowing }?.dismiss()
    }

    override fun showToast(msg: String) {
        if (msg.isNotBlank()) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun finishView() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissLoading()
    }
}