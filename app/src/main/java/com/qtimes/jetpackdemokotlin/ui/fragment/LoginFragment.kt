/**
 * Created with JackHou
 * Date: 2021/4/2
 * Time: 15:30
 * Description:登录界面
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.os.Bundle
import android.view.View
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.databinding.FragmentLoginBinding
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import com.qtimes.jetpackdemokotlin.viewmodel.WelcomeViewModel
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : BaseFragment() {

    private val welcomeViewModel: WelcomeViewModel by getViewModel(WelcomeViewModel::class.java)

    override fun getLayoutId(): Int {
        return R.layout.fragment_login
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_register.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            mNavController.navigate(action)
        }

        btn_login.setOnClickListener {
            welcomeViewModel.login()
        }
    }

    override fun bindingSetViewModels() {
        val fragmentLoginBinding = viewDataBinding as FragmentLoginBinding
        fragmentLoginBinding.welcomeViewModel = welcomeViewModel
        welcomeViewModel.owner = mLifecycleOwner
    }
}