/**
 * Created with JackHou
 * Date: 2021/5/10
 * Time: 16:08
 * Description:激活信息确认界面
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.databinding.FragmentAtcInfoConfirmBinding
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import com.qtimes.jetpackdemokotlin.viewmodel.AtcInfoConfirmViewModel
import kotlinx.android.synthetic.main.fragment_atc_info_confirm.*

/**
 * Author: JackHou
 * Date: 2021/5/10.
 * 激活信息确认界面
 */
class AtcInfoConfirmFragment : BaseFragment() {

    private val atcInfoConfirmViewModel by getViewModel(AtcInfoConfirmViewModel::class.java)

    override fun getLayoutId(): Int {
        return R.layout.fragment_atc_info_confirm
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeArgs: AtcInfoConfirmFragmentArgs by navArgs()
        val doorInfo = safeArgs.doorType
        val cameraInfo = safeArgs.cameraInfo
        atcInfoConfirmViewModel.doorInfo.postValue(doorInfo)
        atcInfoConfirmViewModel.cameraAngle.postValue(cameraInfo)
        LogUtil.d("doorInfo: ${doorInfo.name},  cameraInfo: ${cameraInfo.angle}")
        back_title_atc_info.onBackClickListener {
            mNavController.navigateUp()
        }

        btn_door_activate_confirm.setOnClickListener {

        }
    }

    override fun bindingSetViewModels() {
        super.bindingSetViewModels()
        val fragmentAtcInfoConfirmBinding = viewDataBinding as FragmentAtcInfoConfirmBinding
        fragmentAtcInfoConfirmBinding.atcConfirmVM = atcInfoConfirmViewModel
    }
}