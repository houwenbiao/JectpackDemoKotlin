/**
 * Created with JackHou
 * Date: 2021/3/15
 * Time: 18:11
 * Description:MainActivity界面,包含了fragment
 */

package com.qtimes.jetpackdemokotlin.ui.activity

import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.descriptionText
import com.mikepenz.materialdrawer.model.interfaces.iconRes
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.mikepenz.materialdrawer.model.interfaces.nameText
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.databinding.ActivityMainBinding
import com.qtimes.jetpackdemokotlin.ui.base.BaseActivity
import com.qtimes.jetpackdemokotlin.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mNavController: NavController? = null
    private val mainViewModel by getViewModel(MainViewModel::class.java)
    private var mExitTime: Long = 0
    private var mLastBackClickTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentManager = supportFragmentManager
        val navHostFragment =
            fragmentManager.findFragmentById(R.id.main_fragment) as NavHostFragment?
        navHostFragment?.let {
            mNavController = it.navController
            bottom_nav_view.setupWithNavController(it.navController)
        }

        //DrawerLayout
        val item1 = PrimaryDrawerItem().apply { nameRes = R.string.app_name; identifier = 1 }
        val item2 = SecondaryDrawerItem().apply { nameRes = R.string.user_name; identifier = 2 }
        slider.itemAdapter.add(item1, DividerDrawerItem(), item2, SecondaryDrawerItem())
        slider.accountHeader = AccountHeaderView(this).apply {
            addProfiles(ProfileDrawerItem().apply {
                nameText = "Jack Hou"; descriptionText = "jackhou1990@163.com"; iconRes =
                R.drawable.ic_account; identifier = 102
            })
            onAccountHeaderListener = { view, profile, current -> false }
            withSavedInstance(savedInstanceState)
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                val currentTime = System.currentTimeMillis()
                mExitTime = currentTime - mLastBackClickTime
                mLastBackClickTime = currentTime
                if (mExitTime < 2000) {
                    finish()
                } else {
                    showToast(mContext!!.getString(R.string.exit_toast))
                    return false
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}