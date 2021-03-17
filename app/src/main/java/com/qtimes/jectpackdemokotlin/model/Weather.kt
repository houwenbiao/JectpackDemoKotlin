/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 14:19
 * Description:
 */

package com.qtimes.jectpackdemokotlin.model


class Weather {
    var data: InnerWeather? = null

    class InnerWeather {
        var weather: List<NearestWeather>? = null

        class NearestWeather {
            /**
             * date : 2018-10-26
             * info : {"dawn":["7","小雨","16","东风","微风","17:13"],"day":["1","多云","22","西北风","3-5级","06:03"],"night":["0","晴","13","西北风","5-6级","17:12"]}
             * week : 五
             * nongli : 九月十八
             */
            var date: String? = null
            var info: InfoBean? = null
            var week: String? = null
            var nongli: String? = null

            class InfoBean {
                var dawn: List<String>? = null
                var day: List<String>? = null
                var night: List<String>? = null
            }
        }
    }
}