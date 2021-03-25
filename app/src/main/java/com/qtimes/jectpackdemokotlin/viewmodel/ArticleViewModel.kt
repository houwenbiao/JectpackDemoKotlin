/**
 * Created with JackHou
 * Date: 2021/3/22
 * Time: 19:42
 * Description:
 */

package com.qtimes.jectpackdemokotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kuky.demo.wan.android.entity.HotKeyData
import com.qtimes.jectpackdemokotlin.viewmodel.base.BaseViewModel


class ArticleViewModel :
    BaseViewModel() {
    private var hotKeys: MutableLiveData<MutableList<HotKeyData>> = MutableLiveData()
}