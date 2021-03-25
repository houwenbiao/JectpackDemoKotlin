/**
 * Created with JackHou
 * Date: 2021/3/16
 * Time: 11:20
 * Description:
 */

package com.qtimes.jectpackdemokotlin.ui.fragment

import android.os.Bundle
import android.view.View
import com.qtimes.jectpackdemokotlin.R
import com.qtimes.jectpackdemokotlin.databinding.FragmentNewsBinding
import com.qtimes.jectpackdemokotlin.ui.base.BaseFragment
import com.qtimes.jectpackdemokotlin.utils.LogUtil
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.coroutines.*


class NewsFragment : BaseFragment() {
    lateinit var fragmentNewsBinding: FragmentNewsBinding
    lateinit var job: Job
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.d(Thread.currentThread().name)
        job = GlobalScope.launch(Dispatchers.Main) {
            delay(2000)
            LogUtil.d(Thread.currentThread().name)
            query_news.text = "开始查询"
        }
        LogUtil.i("---------++++++++")
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_news
    }

    override fun bindingSetViewModels() {
        fragmentNewsBinding = viewDataBinding as FragmentNewsBinding
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}