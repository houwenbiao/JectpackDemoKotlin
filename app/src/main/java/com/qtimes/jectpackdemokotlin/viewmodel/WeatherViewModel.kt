/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 16:13
 * Description:Weather ViewModel层
 */

package com.qtimes.jectpackdemokotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import com.qtimes.jectpackdemokotlin.model.Weather
import com.qtimes.jectpackdemokotlin.net.base.BaseException
import com.qtimes.jectpackdemokotlin.net.base.RequestMultiplyCallback
import com.qtimes.jectpackdemokotlin.net.datasource.WeatherRemoteDataSource
import com.qtimes.jectpackdemokotlin.repository.base.WeatherRepository
import com.qtimes.jectpackdemokotlin.utils.LogUtil
import com.qtimes.jectpackdemokotlin.viewmodel.base.BaseViewModel


class WeatherViewModel(
    private val weatherRepository: WeatherRepository = WeatherRepository(
        WeatherRemoteDataSource()
    )
) : BaseViewModel() {


    var cityName = MutableLiveData<String>()


    fun queryWeather() {
        cityName.value?.let {
            LogUtil.d(it)
            weatherRepository.queryWeather(it, object : RequestMultiplyCallback<Weather> {
                override fun onSuccess(t: Weather) {

                }

                override fun onFail(exception: BaseException) {

                }

            })
        }
    }
}