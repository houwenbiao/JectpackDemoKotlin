/**
 * Created with JackHou
 * Date: 2021/4/2
 * Time: 15:30
 * Description:
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.databinding.FragmentLoginBinding
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.viewmodel.WelcomeViewModel


class LoginFragment : BaseFragment() {

    private val welcomeViewModel: WelcomeViewModel by getViewModel(WelcomeViewModel::class.java)

    override fun getLayoutId(): Int {
        return R.layout.fragment_login
    }

    override fun bindingSetViewModels() {
        val fragmentLoginBinding = viewDataBinding as FragmentLoginBinding
        fragmentLoginBinding.welcomeViewModel = welcomeViewModel
    }
}