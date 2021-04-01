/**
 * Created with JackHou
 * Date: 2021/3/23
 * Time: 18:31
 * Description:服务端DataSource
 */

package com.qtimes.jectpackdemokotlin.net.datasource

import com.qtimes.jectpackdemokotlin.model.GithubRepository
import com.qtimes.jectpackdemokotlin.net.HttpConfig
import com.qtimes.jectpackdemokotlin.net.base.IUIActionEvent
import com.qtimes.jectpackdemokotlin.net.service.ApiService


class MainRemoteDataSource(iUIActionEvent: IUIActionEvent) :
    RemoteExtendDataSource<ApiService, GithubRepository>(iUIActionEvent, ApiService::class.java) {

    override val baseUrl: String
        get() = HttpConfig.BASE_URL_WEATHER

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GithubRepository> {
        return LoadResult.Error(Exception())
    }
}