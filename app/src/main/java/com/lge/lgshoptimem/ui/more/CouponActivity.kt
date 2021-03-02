package com.lge.lgshoptimem.ui.more

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityCouponBinding
import com.lge.lgshoptimem.ui.common.ActionBar

class CouponActivity : AppCompatActivity() , ActionBar.onActionBarListener {

    private lateinit var mBinding: ActivityCouponBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_coupon)

        //actionbar
        val actionBar = ActionBar(this)
        actionBar.title = getString(R.string.coupon)
        actionBar.setButton(ActionBar.ACTION_BACK, ActionBar.ACTION_NONE)
        mBinding.actionbar = actionBar
    }

    override fun onLeft() {
        onBackPressed()
    }

    override fun onRight() {
        //nothins in here
    }
}