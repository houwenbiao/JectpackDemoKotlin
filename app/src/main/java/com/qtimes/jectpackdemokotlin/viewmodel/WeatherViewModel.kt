/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 16:13
 * Description:Weather ViewModel层
 */

package com.qtimes.jectpackdemokotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import com.qtimes.jectpackdemokotlin.model.DistrictBean
import com.qtimes.jectpackdemokotlin.repository.WeatherRepository
import com.qtimes.jectpackdemokotlin.utils.LogUtil
import com.qtimes.jectpackdemokotlin.viewmodel.base.BaseViewModel
import java.util.function.Consumer


class WeatherViewModel : BaseViewModel() {
    var weatherRepository: WeatherRepository = WeatherRepository(this)

    var cityName = MutableLiveData<String>()
    var mProvinces = MutableLiveData<List<DistrictBean>>()

    fun queryProvince() {
        weatherRepository.queryProvince {
            onSuccess {
                mProvinces.postValue(it)
                it.forEach { it1 ->
                    LogUtil.d(it1.name)
                }

            }

        }
    }

    fun queryWeather() {
        cityName.value?.let { it ->
            LogUtil.d(it)
            weatherRepository.queryWeather(it) {
                onSuccess { it0 ->
                    it0.forEach(Consumer { it1 ->
                        it1.casts.forEach { it2 ->
                            LogUtil.d(
                                "daypower: " + it2.daypower +
                                        ", dayweather: " + it2.dayweather
                            )
                        }
                    })
                }
            }
        }
    }
}