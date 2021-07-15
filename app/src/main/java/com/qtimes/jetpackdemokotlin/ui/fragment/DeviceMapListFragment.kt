/**
 * Created with JackHou
 * Date: 2021/7/15
 * Time: 11:06
 * Description:设备绑定关系列表Fragment
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.databinding.FragmentDeviceMapListBinding
import com.qtimes.jetpackdemokotlin.paging.adapter.DeviceMapAdapter
import com.qtimes.jetpackdemokotlin.paging.adapter.FooterAdapter
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.viewmodel.ConstructionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class DeviceMapListFragment : BaseFragment() {
    private val constructionViewModel: ConstructionViewModel by getViewModel(ConstructionViewModel::class.java)
    private lateinit var binding: FragmentDeviceMapListBinding
    private val deviceMapAdapter = DeviceMapAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewDevMap.adapter =
            deviceMapAdapter.withLoadStateFooter(FooterAdapter { deviceMapAdapter.retry() })
        binding.recyclerViewDevMap.layoutManager = LinearLayoutManager(mContext)
        lifecycleScope.launch {
            constructionViewModel.findAll().collectLatest {
                deviceMapAdapter.submitData(it)
            }
        }

        binding.backTitleDevMapList.onBackClickListener {
            mNavController.navigateUp()
        }

        deviceMapAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> {
                    binding.progressBarDevMap.visibility = View.INVISIBLE
                    binding.recyclerViewDevMap.visibility = View.VISIBLE
                }
                is LoadState.Loading -> {
                    binding.progressBarDevMap.visibility = View.VISIBLE
                    binding.recyclerViewDevMap.visibility = View.INVISIBLE
                }
                is LoadState.Error -> {
                    val state = it.refresh as LoadState.Error
                    binding.progressBarDevMap.visibility = View.INVISIBLE
                    showToast("Load Error: ${state.error.message}")
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_device_map_list
    }

    override fun bindingSetViewModels() {
        super.bindingSetViewModels()
        binding = viewDataBinding as FragmentDeviceMapListBinding

    }
}