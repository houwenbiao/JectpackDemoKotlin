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
import kotlinx.android.synthetic.main.back_layout.*
import kotlinx.android.synthetic.main.fragment_atc.*


class AtcFragment : BaseFragment() {

    private val deviceViewModel: DeviceViewModel by getViewModel(DeviceViewModel::class.java)

    override fun getLayoutId(): Int {
        return R.layout.fragment_atc
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deviceViewModel.atcDevice()
        txt_atc_again.setOnClickListener {
            deviceViewModel.atcDevice()
        }
        atc_title.onBackClickListener {
            mNavController.navigateUp()
        }
        btn_go_activate.setOnClickListener {
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
                    btn_go_activate.background =
                        mContext!!.getDrawable(R.drawable.ticket_button_selector)
                }

                AtcState.AUTHENTICATING -> {
                    deviceViewModel.txtAtcStatusColor.postValue(
                        mContext!!.getColor(
                            R.color.txt_state_authenticating
                        )
                    )
                    btn_go_activate.setBackgroundColor(mContext!!.getColor(R.color.btn_unclickable))
                }
                AtcState.UNAUTHENTICATED -> {
                    deviceViewModel.txtAtcStatusColor.postValue(
                        mContext!!.getColor(
                            R.color.txt_state_unauthenticated
                        )
                    )
                    btn_go_activate.setBackgroundColor(mContext!!.getColor(R.color.btn_unclickable))
                }
                AtcState.AUTHENTICATE_FAILED -> {
                    deviceViewModel.txtAtcStatusColor.postValue(
                        mContext!!.getColor(
                            R.color.txt_state_unauthenticated
                        )
                    )
                    btn_go_activate.setBackgroundColor(mContext!!.getColor(R.color.btn_unclickable))
                }
                else -> {
                    deviceViewModel.txtAtcStatusColor.postValue(
                        mContext!!.getColor(
                            R.color.txt_state_unauthenticated
                        )
                    )
                    btn_go_activate.setBackgroundColor(mContext!!.getColor(R.color.btn_unclickable))
                }
            }
        }
    }

    override fun bindingSetViewModels() {
        val mFragmentAtcBinding = viewDataBinding as FragmentAtcBinding
        mFragmentAtcBinding.devVM = deviceViewModel
    }
}