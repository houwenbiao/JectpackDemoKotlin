/**
 * Created with JackHou
 * Date: 2021/3/16
 * Time: 14:57
 * Description:ViewModel的生成类
 */

package com.qtimes.jectpackdemokotlin.viewmodel.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController


class JViewModelProvider {
    companion object {
        fun <T : BaseViewModel?> get(activity: FragmentActivity, clazz: Class<T>): T {
            return ViewModelProvider(activity)[clazz]
        }

        fun <T : BaseViewModel> get(fragment: Fragment, clazz: Class<T>): T {
            return ViewModelProvider(fragment)[clazz]
        }

        fun <T : BaseViewModel> get(
            activity: FragmentActivity,
            clazz: Class<T>,
            navController: NavController
        ): T {
            val t = ViewModelProvider(activity)[clazz]
            t.mNavController = navController
            return t
        }

        fun <T : BaseViewModel> get(
            fragment: Fragment,
            clazz: Class<T>,
            navController: NavController
        ): T {
            val t = ViewModelProvider(fragment)[clazz]
            t.mNavController = navController
            return t
        }
    }
}