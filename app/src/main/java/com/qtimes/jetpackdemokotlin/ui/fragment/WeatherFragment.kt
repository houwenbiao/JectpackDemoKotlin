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
import com.qtimes.jetpackdemokotlin.databinding.FragmentWeatherBinding
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.viewmodel.WeatherViewModel


class WeatherFragment : BaseFragment() {
    lateinit var fragmentWeatherBinding: FragmentWeatherBinding
    private val weatherViewModel by getViewModel(WeatherViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherViewModel.queryProvince()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_weather
    }

    override fun bindingSetViewModels() {
        fragmentWeatherBinding = viewDataBinding as FragmentWeatherBinding
        fragmentWeatherBinding.weatherViewModel = weatherViewModel
    }
}