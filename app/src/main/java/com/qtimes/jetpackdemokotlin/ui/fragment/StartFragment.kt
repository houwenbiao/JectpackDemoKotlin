/**
 * Created with JackHou
 * Date: 2021/4/2
 * Time: 11:41
 * Description:APP启动界面
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.os.Bundle
import android.view.View
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.databinding.FragmentStartBinding
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.viewmodel.WelcomeViewModel
import kotlinx.android.synthetic.main.fragment_start.*


class StartFragment : BaseFragment() {

    private val welcomeViewModel: WelcomeViewModel by getViewModel(WelcomeViewModel::class.java)

    override fun getLayoutId(): Int {
        return R.layout.fragment_start
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_jump.setOnClickListener {
            val action = StartFragmentDirections.actionStartFragmentToLoginFragment()
            mNavController.navigate(action)
        }
        /*welcomeViewModel.timerCount.observe(mLifecycleOwner) {
            if (it == 1) {
                val action = StartFragmentDirections.actionStartFragmentToLoginFragment()
                mNavController.navigate(action)
            }
        }*/
    }

    override fun bindingSetViewModels() {
        val fragmentStartBinding = viewDataBinding as FragmentStartBinding
        fragmentStartBinding.welcomeViewModel = welcomeViewModel
    }
}