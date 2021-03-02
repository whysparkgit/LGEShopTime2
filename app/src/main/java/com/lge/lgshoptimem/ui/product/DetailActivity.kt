package com.lge.lgshoptimem.ui.product

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityDetailBinding
import com.lge.lgshoptimem.model.dto.ProductDetail
import com.lge.lgshoptimem.model.dto.WatchNow
import com.lge.lgshoptimem.ui.common.ActionBar
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.ComponentItemListener

class DetailActivity : AppCompatActivity(), ActionBar.onActionBarListener, ComponentItemListener
{
    private lateinit var mBinding: ActivityDetailBinding

    private var mPartnerId: String = ""
    private var mProductId: String = ""
    private var mCurationId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        mPartnerId = intent.getStringExtra(AppConst.KEY.PARTNER_ID).let { if (it.isNullOrEmpty()) "" else it }
        mProductId = intent.getStringExtra(AppConst.KEY.PRODUCT_ID).let { if (it.isNullOrEmpty()) "" else it }
        mCurationId = intent.getStringExtra(AppConst.KEY.CURATION_ID).let { if (it.isNullOrEmpty()) "" else it }

        init()
    }

    fun init() {
        initActionBar()

        val bundle: Bundle = Bundle()
        bundle.putString(AppConst.KEY.PARTNER_ID, mPartnerId)
        bundle.putString(AppConst.KEY.PRODUCT_ID, mProductId)
        bundle.putString(AppConst.KEY.CURATION_ID, mCurationId)
        bundle.putBoolean(AppConst.KEY.LAUNCH_FROM, false)

        supportFragmentManager.commit {
            add<DetailFragment>(R.id.fc_product_detail, null, bundle)
        }
    }

    private fun initActionBar() {
        val actionBar = ActionBar(this)
        actionBar.title = "DETAIL"
        actionBar.setButton(ActionBar.ACTION_BACK, ActionBar.ACTION_NONE)
        mBinding.actionbar = actionBar
    }

    override fun onLeft() {
        finish()
    }

    override fun onRight() {
        finish()
    }

    override fun onClick(v: View, pos: Int) {
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
    }
}