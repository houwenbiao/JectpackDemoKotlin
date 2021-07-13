/**
 * Created with JackHou
 * Date: 2021/4/26
 * Time: 17:20
 * Description:设备认证界面
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.databinding.FragmentAtcBinding
import com.qtimes.jetpackdemokotlin.model.AtcState
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.viewmodel.DeviceViewModel


class AtcFragment : BaseFragment() {

    private val deviceViewModel: DeviceViewModel by getViewModel(DeviceViewModel::class.java)
    private lateinit var binding: FragmentAtcBinding;
    override fun getLayoutId(): Int {
        return R.layout.fragment_atc
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deviceViewModel.atcDevice()
        binding.txtAtcAgain.setOnClickListener {
            deviceViewModel.atcDevice()
        }
        binding.atcTitle.onBackClickListener {
            mNavController.navigateUp()
        }
        binding.btnGoActivate.setOnClickListener {
            val action = AtcFragmentDirections.actionAtcFragmentToDoorListFragment()
            mNavController.navigate(action)
        }
        deviceViewModel.atcState.observe(mLifecycleOwner) {
            when (it) {
                AtcState.AUTHENTICATED -> {
                    deviceViewModel.txtAtcStatusColor.postValue(
                        mContext!!.getColor(
                            R.color.txt_state_authenticated
                        )
                    )
                    binding.btnGoActivate.background =
                        mContext!!.getDrawable(R.drawable.ticket_button_selector)
                }

                AtcState.AUTHENTICATING -> {
                    deviceViewModel.txtAtcStatusColor.postValue(
                        mContext!!.getColor(
                            R.color.txt_state_authenticating
                        )
                    )
                    binding.btnGoActivate.setBackgroundColor(mContext!!.getColor(R.color.btn_unclickable))
                }
                AtcState.UNAUTHENTICATED -> {
                    deviceViewModel.txtAtcStatusColor.postValue(
                        mContext!!.getColor(
                            R.color.txt_state_unauthenticated
                        )
                    )
                    binding.btnGoActivate.setBackgroundColor(mContext!!.getColor(R.color.btn_unclickable))
                }
                AtcState.AUTHENTICATE_FAILED -> {
                    deviceViewModel.txtAtcStatusColor.postValue(
                        mContext!!.getColor(
                            R.color.txt_state_unauthenticated
                        )
                    )
                    binding.btnGoActivate.setBackgroundColor(mContext!!.getColor(R.color.btn_unclickable))
                }
                else -> {
                    deviceViewModel.txtAtcStatusColor.postValue(
                        mContext!!.getColor(
                            R.color.txt_state_unauthenticated
                        )
                    )
                    binding.btnGoActivate.setBackgroundColor(mContext!!.getColor(R.color.btn_unclickable))
                }
            }
        }
    }

    override fun bindingSetViewModels() {
        binding = viewDataBinding as FragmentAtcBinding
        binding.devVM = deviceViewModel
    }
}