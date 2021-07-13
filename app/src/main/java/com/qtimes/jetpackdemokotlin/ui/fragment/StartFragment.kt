/**
 * Created with JackHou
 * Date: 2021/4/2
 * Time: 11:41
 * Description:APP启动界面
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.asLiveData
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.common.Const
import com.qtimes.jetpackdemokotlin.databinding.FragmentStartBinding
import com.qtimes.jetpackdemokotlin.model.UserState
import com.qtimes.jetpackdemokotlin.ui.activity.MainActivity
import com.qtimes.jetpackdemokotlin.ui.activity.WelcomeActivity
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.utils.ActivityMgr
import com.qtimes.jetpackdemokotlin.utils.DataStoreUtil
import com.qtimes.jetpackdemokotlin.viewmodel.WelcomeViewModel


class StartFragment : BaseFragment() {

    private val welcomeViewModel: WelcomeViewModel by getViewModel(WelcomeViewModel::class.java)
    private val actionStart2Login = StartFragmentDirections.actionStartFragmentToLoginFragment()
    private lateinit var binding: FragmentStartBinding

    override fun getLayoutId(): Int {
        return R.layout.fragment_start
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnJump.setOnClickListener {
            jumpToHome()
        }
        welcomeViewModel.timerCount.observe(mLifecycleOwner) {
            if (it == 0) {
                jumpToHome()
            }
        }
    }

    /**
     * 广告界面结束跳转到指定界面
     */
    private fun jumpToHome() {
        DataStoreUtil.getString(Const.KEY_USER_STATE).asLiveData()
            .observe(mLifecycleOwner) { state ->
                if (state == UserState.ONLINE.name) {
                    mContext?.let {
                        val intentMain = Intent(it, MainActivity::class.java)
                        it.startActivity(intentMain)
                        ActivityMgr.removeActivity(WelcomeActivity::class.java)
                    }
                } else {
                    mNavController.navigate(actionStart2Login)
                }
            }
    }

    override fun bindingSetViewModels() {
        binding = viewDataBinding as FragmentStartBinding
        binding.welcomeViewModel = welcomeViewModel
    }
}