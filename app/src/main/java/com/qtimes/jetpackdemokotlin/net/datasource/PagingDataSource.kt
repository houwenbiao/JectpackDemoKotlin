/**
 * Created with JackHou
 * Date: 2021/3/31
 * Time: 19:57
 * Description:
 */

package com.qtimes.jetpackdemokotlin.net.datasource

import androidx.paging.PagingState
import com.qtimes.jetpackdemokotlin.model.GithubRepository
import com.qtimes.jetpackdemokotlin.net.HttpConfig
import com.qtimes.jetpackdemokotlin.net.base.IUIActionEvent
import com.qtimes.jetpackdemokotlin.net.service.ApiService


class ArticleDataSource(iUIActionEvent: IUIActionEvent) :
    RemoteExtendDataSource<ApiService, GithubRepository>(iUIActionEvent, ApiService::class.java) {
    override val baseUrl: String
        get() = HttpConfig.BASE_URL_GITHUB


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GithubRepository> {

        return try {
            val page = params.key ?: 1//当前的页数
            val pageSize = params.loadSize//每一页包含多少条数据
            val res = getApiService(baseUrl).queryRepositories(page, pageSize)
            val data = res.data
            val lastPage = if (page > 1) page - 1 else null//上一页
            val nextPage = if (data.isNotEmpty()) page + 1 else null//下一页
            LoadResult.Page(data, lastPage, nextPage)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GithubRepository>): Int? {
        return null
    }
}

