package com.lge.shoptime.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.lge.shoptime.R
import com.lge.shoptime.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityHomeBinding
    private lateinit var mAdapter: FragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        init()
    }

    fun init() {
        mAdapter = FragmentAdapter(this)
        mBinding.mmVpPager.setAdapter(mAdapter)

        val strategy =
            TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.text = "FOR YOU"
                    1 -> tab.text = "WATCH NOW"
                    2 -> tab.text = "HOT PICKS"
                }
            }

        TabLayoutMediator(mBinding.mmTlTabs, mBinding.mmVpPager, true, strategy).attach()
    }

    fun onBack() {
        onBackPressed()
    }
}