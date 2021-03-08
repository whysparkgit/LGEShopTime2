package com.lge.lgshoptimem.ui.more

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityForyouSettingBinding
import com.lge.lgshoptimem.ui.common.ActionBar
import com.lge.lgshoptimem.ui.common.AppConst

class ForYouSettingActivity : AppCompatActivity() , ActionBar.onActionBarListener {
    private lateinit var mBinding: ActivityForyouSettingBinding
    private lateinit var mAdapter: ForYouSettingFragmentAdapter
    private val mViewModel: ForYouSettingViewModel by viewModels()
    var mLaunchFrom: Int = AppConst.VALUE.INITIAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_foryou_setting)

        mLaunchFrom = intent.getIntExtra(AppConst.KEY.LAUNCH_FROM, AppConst.VALUE.INITIAL)

        init()
    }

    fun init() {
        initActionBar()
        mAdapter = ForYouSettingFragmentAdapter(this)
        mBinding.fysVpPager.adapter = mAdapter
        mBinding.launchFrom = mLaunchFrom

        val strategy =
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.text = "CATEGORIES"
                    1 -> tab.text = "KEYWORDS"
                }
            }

        if (mLaunchFrom == AppConst.VALUE.KEYWORD) mBinding.fysVpPager.setCurrentItem(1, false)
        TabLayoutMediator(mBinding.fysTlTabs, mBinding.fysVpPager, true, strategy).attach()
    }

    private fun initActionBar() {
        val actionBar = ActionBar(this)
        actionBar.title = getString(R.string.for_you_setting)
        actionBar.setButton(ActionBar.ACTION_BACK, ActionBar.ACTION_NONE)
        mBinding.actionbar = actionBar
    }

    override fun onLeft() {
        onBackPressed()
    }

    override fun onRight() {
        //nothing
    }
}