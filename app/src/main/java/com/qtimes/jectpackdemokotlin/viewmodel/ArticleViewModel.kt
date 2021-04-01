/**
 * Created with JackHou
 * Date: 2021/4/1
 * Time: 14:05
 * Description:Article相关的ViewModel层业务代码
 */

package com.qtimes.jectpackdemokotlin.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.qtimes.jectpackdemokotlin.model.GithubRepository
import com.qtimes.jectpackdemokotlin.repository.ArticleRepository
import com.qtimes.jectpackdemokotlin.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.Flow


class ArticleViewModel : BaseViewModel() {

    private val articleRepository = ArticleRepository(this)

    fun queryGithubRepository(): Flow<PagingData<GithubRepository>> {
        return articleRepository.queryRepository().cachedIn(viewModelScope)
    }
}