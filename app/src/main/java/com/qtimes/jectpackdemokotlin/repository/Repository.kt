/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 16:01
 * Description:
 */

package com.qtimes.jectpackdemokotlin.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.qtimes.jectpackdemokotlin.model.DistrictBean
import com.qtimes.jectpackdemokotlin.model.ForecastsBean
import com.qtimes.jectpackdemokotlin.model.GithubRepository
import com.qtimes.jectpackdemokotlin.net.base.IUIActionEvent
import com.qtimes.jectpackdemokotlin.net.base.RequestCallback
import com.qtimes.jectpackdemokotlin.net.datasource.ArticleDataSource
import com.qtimes.jectpackdemokotlin.repository.base.BaseRepository
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

