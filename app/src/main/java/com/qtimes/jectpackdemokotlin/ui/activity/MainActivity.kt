/**
 * Created with JackHou
 * Date: 2021/3/15
 * Time: 18:11
 * Description:MainActivity界面,包含了fragment
 */

package com.qtimes.jectpackdemokotlin.ui.activity

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.qtimes.jectpackdemokotlin.R
import com.qtimes.jectpackdemokotlin.databinding.ActivityMainBinding
import com.qtimes.jectpackdemokotlin.ui.base.BaseActivity
import com.qtimes.jectpackdemokotlin.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    var mNavController: NavController? = null
    private val mainViewModel by getViewModel(MainViewModel::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentManager = supportFragmentManager
        val navHostFragment =
            fragmentManager.findFragmentById(R.id.main_fragment) as NavHostFragment?
        navHostFragment?.let {
            mNavController = it.navController
            bottom_nav_view.setupWithNavController(it.navController)
        }
    }


    override fun getNavController(): NavController? {
        return mNavController
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun bindingSetViewModels() {
        binding = viewDataBinding as ActivityMainBinding
        binding.mainViewModel = mainViewModel
    }
}