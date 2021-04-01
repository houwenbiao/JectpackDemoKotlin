/**
 * Created with JackHou
 * Date: 2021/3/16
 * Time: 11:18
 * Description:Github仓库列表显示
 */

package com.qtimes.jetpackdemokotlin.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.databinding.FragmentGithubRepositoryBinding
import com.qtimes.jetpackdemokotlin.paging.adapter.FooterAdapter
import com.qtimes.jetpackdemokotlin.paging.adapter.RepositoryAdapter
import com.qtimes.jetpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jetpackdemokotlin.viewmodel.ArticleViewModel
import kotlinx.android.synthetic.main.fragment_github_repository.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class GithubRepositoryFragment : BaseFragment() {

    lateinit var fragmentArticleBinding: FragmentGithubRepositoryBinding
    private val articleViewModel by getViewModel(ArticleViewModel::class.java)
    private val repositoryAdapter = RepositoryAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.adapter =
            repositoryAdapter.withLoadStateFooter(FooterAdapter { repositoryAdapter.retry() })
        recycler_view.layoutManager = LinearLayoutManager(mContext)
        lifecycleScope.launch {
            articleViewModel.queryGithubRepository().collectLatest { pagingData ->
                repositoryAdapter.submitData(pagingData)
            }
        }

        repositoryAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> {
                    progress_bar.visibility = View.INVISIBLE
                    recycler_view.visibility = View.VISIBLE
                }
                is LoadState.Loading -> {
                    progress_bar.visibility = View.VISIBLE
                    recycler_view.visibility = View.INVISIBLE
                }
                is LoadState.Error -> {
                    val state = it.refresh as LoadState.Error
                    progress_bar.visibility = View.INVISIBLE
                    Toast.makeText(
                        mContext,
                        "Load Error: ${state.error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_github_repository
    }

    override fun bindingSetViewModels() {
        fragmentArticleBinding = viewDataBinding as FragmentGithubRepositoryBinding
    }
}