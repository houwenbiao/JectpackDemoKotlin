/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 16:00
 * Description:
 */

package com.qtimes.jetpackdemokotlin.repository.base

import com.qtimes.jetpackdemokotlin.net.base.IUIActionEvent
import com.qtimes.jetpackdemokotlin.net.datasource.MainRemoteDataSource
import com.qtimes.jetpackdemokotlin.net.datasource.ArticleDataSource


open class BaseRepository(iUIActionEvent: IUIActionEvent) {
    /**
     * 正常来说单个项目中应该只有一个 RemoteDataSource 实现类，即全局使用同一份配置
     * 但父类也应该允许子类使用一个单独的 RemoteDataSource
     */
    protected open val mainRemoteDataSource by lazy {
        MainRemoteDataSource(iUIActionEvent)
    }

    protected open val repositoryDataSource by lazy {
        ArticleDataSource(iUIActionEvent)
    }
}