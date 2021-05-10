/**
 * Created with JackHou
 * Date: 2021/3/16
 * Time: 9:59
 * Description:
 */

package com.qtimes.jetpackdemokotlin.ui.base

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.net.base.IUIActionEventObserver
import com.qtimes.jetpackdemokotlin.utils.ActivityMgr
import com.qtimes.jetpackdemokotlin.utils.LogUtil
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

    override val mLifecycleOwner: LifecycleOwner
        get() = viewLifecycleOwner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner//建立双向绑定的关键
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = findNavController()
        progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        bindingSetViewModels()
    }

    abstract fun getLayoutId(): Int

    //ViewDataBinding set model
    open fun bindingSetViewModels() {}


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

    override val mLifecycleOwner: LifecycleOwner
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        super.onCreate(savedInstanceState)
        ActivityMgr.addActivity(this)
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

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityMgr.destroyActivity(this)
        dismissLoading()
    }
}