/**
 * Created with JackHou
 * Date: 2021/3/16
 * Time: 11:17
 * Description:天气请求界面
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.os.Bundle
import android.view.View
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.databinding.FragmentDeviceBinding
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.viewmodel.DeviceViewModel


class DeviceFragment : BaseFragment() {
    lateinit var binding: FragmentDeviceBinding
    private val devModel by getViewModel(DeviceViewModel::class.java)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnJumpAtcPage.setOnClickListener {
            mNavController.navigate(DeviceFragmentDirections.actionDeviceFragmentToAtcFragment())
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_device
    }

    override fun bindingSetViewModels() {
        binding = viewDataBinding as FragmentDeviceBinding
        binding.devVM = devModel
    }
}