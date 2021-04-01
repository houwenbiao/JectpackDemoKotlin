/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 16:01
 * Description:
 */

package com.qtimes.jetpackdemokotlin.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.qtimes.jetpackdemokotlin.model.DistrictBean
import com.qtimes.jetpackdemokotlin.model.ForecastsBean
import com.qtimes.jetpackdemokotlin.model.GithubRepository
import com.qtimes.jetpackdemokotlin.net.base.IUIActionEvent
import com.qtimes.jetpackdemokotlin.net.base.RequestCallback
import com.qtimes.jetpackdemokotlin.net.datasource.ArticleDataSource
import com.qtimes.jetpackdemokotlin.repository.base.BaseRepository
import kotlinx.coroutines.flow.Flow


class WeatherRepository(iUIActionEvent: IUIActionEvent) : BaseRepository(iUIActionEvent) {

    /**
     * 查询省份
     */
    fun queryProvince(callbackFun: (RequestCallback<List<DistrictBean>>.() -> Unit)? = null) {
        mainRemoteDataSource.enqueueLoading({ queryProvince() }, callbackFun = callbackFun)
    }

    /**
     * 查询指定城市天气
     */
    fun queryWeather(
        cityName: String,
        callbackFun: (RequestCallback<List<ForecastsBean>>.() -> Unit)? = null
    ) {
        mainRemoteDataSource.enqueueLoading({ queryWeather(cityName) }, callbackFun = callbackFun)
    }
}

class ArticleRepository(private val iUIActionEvent: IUIActionEvent) :
    BaseRepository(iUIActionEvent) {

    /**
     * 查询github仓库
     */
    fun queryRepository(): Flow<PagingData<GithubRepository>> {
        return Pager(
            config = PagingConfig(20),
            pagingSourceFactory = { ArticleDataSource(iUIActionEvent) }).flow
    }
}

