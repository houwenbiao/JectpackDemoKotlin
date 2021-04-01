/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 14:19
 * Description:
 */

package com.qtimes.jectpackdemokotlin.model


data class ForecastsBean(
    val city: String,
    val adcode: String,
    val province: String,
    val reporttime: String,
    val casts: List<CastsBean>
)

data class CastsBean(
    val date: String,
    val week: String,
    val dayweather: String,
    val nightweather: String,
    val daytemp: String,
    val nighttemp: String,
    val daywind: String,
    val nightwind: String,
    val daypower: String,
    val nightpower: String
)

data class DistrictBean(
    val adcode: String,
    val center: String,
    val level: String,
    val name: String,
    val districts: List<DistrictBean>
)