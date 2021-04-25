/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 16:01
 * Description:Repository集合
 */

package com.qtimes.jetpackdemokotlin.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.qtimes.jetpackdemokotlin.model.DistrictBean
import com.qtimes.jetpackdemokotlin.model.ForecastsBean
import com.qtimes.jetpackdemokotlin.model.GithubRepository
import com.qtimes.jetpackdemokotlin.model.User
import com.qtimes.jetpackdemokotlin.net.base.IUIActionEvent
import com.qtimes.jetpackdemokotlin.net.base.RequestCallback
import com.qtimes.jetpackdemokotlin.net.datasource.ArticleDataSource
import com.qtimes.jetpackdemokotlin.repository.base.BaseRepository
import com.qtimes.jetpackdemokotlin.room.AppDatabase
import kotlinx.coroutines.flow.Flow

/**
 * 天气相关Repository
 */
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

/**
 * 文章相关Repository
 */
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

/**
 * 用户相关Repository
 */
class UserRepository(
    private val iUIActionEvent: IUIActionEvent,
    private val appDatabase: AppDatabase
) : BaseRepository(iUIActionEvent) {

    /**
     * 注册用户
     */
    suspend fun resisterUser(user: User): Long {
        return appDatabase.getUserDao().registerUser(user)
    }

    /**
     * 通过用户名或者手机号查找用户
     */
    fun findUserByAccountOrPhone(param: String): LiveData<User?> =
        appDatabase.getUserDao().findUserByAccountOrPhone(param)
}

