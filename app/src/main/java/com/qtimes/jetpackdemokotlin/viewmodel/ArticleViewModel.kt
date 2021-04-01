/**
 * Created with JackHou
 * Date: 2021/4/1
 * Time: 14:05
 * Description:Article相关的ViewModel层业务代码
 */

package com.qtimes.jetpackdemokotlin.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.qtimes.jetpackdemokotlin.model.GithubRepository
import com.qtimes.jetpackdemokotlin.repository.ArticleRepository
import com.qtimes.jetpackdemokotlin.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.Flow


class ArticleViewModel : BaseViewModel() {

    private val articleRepository = ArticleRepository(this)

    fun queryGithubRepository(): Flow<PagingData<GithubRepository>> {
        return articleRepository.queryRepository().cachedIn(viewModelScope)
    }
}