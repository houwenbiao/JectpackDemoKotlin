/**
 * Created with JackHou
 * Date: 2021/4/9
 * Time: 18:14
 * Description:用户注册界面
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.os.Bundle
import android.view.View
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.databinding.FragmentRegisterBinding
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import com.qtimes.jetpackdemokotlin.viewmodel.WelcomeViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.launch


class RegisterFragment : BaseFragment() {

    val welcomeViewModel by getViewModel(WelcomeViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txt_cancel.setOnClickListener {
            mNavController.navigateUp()
        }

        btn_register.setOnClickListener {
            launchMain {
                val success = welcomeViewModel.registerUser()
                LogUtil.d("register user: $success")
                if (success != -1L) {
                    showToast("注册成功，返回登录")
                    mNavController.navigateUp()
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_register
    }

    override fun bindingSetViewModels() {
        val binding: FragmentRegisterBinding = viewDataBinding as FragmentRegisterBinding
        binding.welcomeViewModel = welcomeViewModel
    }
}