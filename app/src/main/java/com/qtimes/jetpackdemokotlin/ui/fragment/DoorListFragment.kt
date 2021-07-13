/**
 * Created with JackHou
 * Date: 2021/5/7
 * Time: 13:38
 * Description:门列表界面
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.common.Const
import com.qtimes.jetpackdemokotlin.databinding.FragmentDoorListBinding
import com.qtimes.jetpackdemokotlin.model.DoorInfo
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import com.qtimes.jetpackdemokotlin.viewmodel.DoorListViewModel


@SuppressLint("UseCompatLoadingForDrawables")
class DoorListFragment : BaseFragment() {

    private val doorListVM: DoorListViewModel by getViewModel(DoorListViewModel::class.java)
    private lateinit var binding: FragmentDoorListBinding
    override fun getLayoutId(): Int {
        return R.layout.fragment_door_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doorListVM.getDoorList()
        binding.doorListTitle.onBackClickListener {
            mNavController.navigateUp()
        }
        binding.doorListSrl.setOnRefreshListener { reFreshLayout ->
            reFreshLayout.finishRefresh(Const.LOADING_TIMEOUT)
            doorListVM.getDoorList()
        }

        binding.btnDoorTypeNextStep.setOnClickListener {
            val action = DoorListFragmentDirections
                .actionDoorListFragmentToCameraTypeFragment(doorListVM.checkDoor.value!!)
            mNavController.navigate(action)
        }

        LogUtil.d("onViewCreated")
        doorListVM.doors.observe(mLifecycleOwner) { doorList ->
            if (doorList.size > 0) {
                binding.btnDoorTypeNextStep.background =
                    mContext!!.getDrawable(R.drawable.ticket_button_selector)
                updateDoorTypeUI(doorList)
            } else {
                binding.btnDoorTypeNextStep.setBackgroundColor(mContext!!.getColor(R.color.btn_unclickable))
            }
        }
    }

    override fun bindingSetViewModels() {
        binding = viewDataBinding as FragmentDoorListBinding
        binding.doorListVM = doorListVM
    }

    /**
     * 更新门列表显示
     */
    private fun updateDoorTypeUI(doorList: MutableList<DoorInfo>) {
        binding.rgDoorType.removeAllViews()
        doorList.forEach { doorInfo ->
            val tempButton = RadioButton(mContext)
            tempButton.setPadding(
                mContext!!.resources.getDimensionPixelSize(R.dimen.x20),
                mContext!!.resources.getDimensionPixelSize(R.dimen.y20), 0,
                mContext!!.resources.getDimensionPixelSize(R.dimen.y20)
            ) // 设置文字距离按钮四周的距离
            tempButton.text = doorInfo.name
            tempButton.id = doorInfo.typeId
            if (doorListVM.checkDoor.value?.typeId == doorInfo.typeId) {
                tempButton.isChecked = true
                doorListVM.checkDoor.postValue(doorInfo)
            } else if (doorInfo.typeId == doorList[0].typeId) {
                tempButton.isChecked = true
                doorListVM.checkDoor.postValue(doorInfo)
            }
            binding.rgDoorType.addView(
                tempButton,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }
}