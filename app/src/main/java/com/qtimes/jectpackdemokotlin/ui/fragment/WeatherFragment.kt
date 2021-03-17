/**
 * Created with JackHou
 * Date: 2021/3/16
 * Time: 11:17
 * Description:天气请求界面
 */

package com.qtimes.jectpackdemokotlin.ui.fragment

import com.qtimes.jectpackdemokotlin.R
import com.qtimes.jectpackdemokotlin.databinding.FragmentWeatherBinding
import com.qtimes.jectpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jectpackdemokotlin.viewmodel.WeatherViewModel
import com.qtimes.jectpackdemokotlin.viewmodel.base.BaseViewModel
import com.qtimes.jectpackdemokotlin.viewmodel.base.JViewModelProvider


class WeatherFragment : BaseFragment() {
    lateinit var weatherViewModel: WeatherViewModel
    lateinit var fragmentWeatherBinding: FragmentWeatherBinding

    override fun getLayoutId(): Int {
        return R.layout.fragment_weather
    }

    override fun initViewModel(): BaseViewModel {
        weatherViewModel =
            JViewModelProvider.get(this, WeatherViewModel::class.java, mNavController)
        return weatherViewModel
    }

    override fun bindingSetViewModels() {
        fragmentWeatherBinding = viewDataBinding as FragmentWeatherBinding
        fragmentWeatherBinding.weatherViewModel = weatherViewModel
    }
}