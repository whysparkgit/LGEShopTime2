package com.lge.lgshoptimem.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityHomeBinding
import com.lge.lgshoptimem.ui.common.FavoriteProductManager
import com.lge.lgshoptimem.ui.common.UpcomingAlarmManager

class HomeActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityHomeBinding
    private lateinit var mAdapter: HomeFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        init()
    }

    fun init() {
        mAdapter = HomeFragmentAdapter(this)
        mBinding.mmVpPager.adapter = mAdapter
        FavoriteProductManager.getInstance()
        UpcomingAlarmManager.getInstance()

        val strategy =
            TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.text = "FOR YOU"
                    1 -> tab.text = "WATCH NOW"
                    2 -> tab.text = "HOT PICKS"
                }
            }

        mBinding.mmVpPager.setCurrentItem(1, false)
        TabLayoutMediator(mBinding.mmTlTabs, mBinding.mmVpPager, true, strategy).attach()
    }

    fun onBack() {
        onBackPressed()
    }

    override fun onDestroy() {
        FavoriteProductManager.getInstance().syncData()
        UpcomingAlarmManager.getInstance().syncData()
        super.onDestroy()
    }
}