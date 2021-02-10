package com.lge.lgshoptimem.ui.product

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
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
    private lateinit var mAdapter: DetailListAdapter
    private val mViewModel: DetailViewModel by viewModels()

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

        if (mProductId.isNullOrEmpty()) return

        mAdapter = DetailListAdapter(this)

        mBinding.dtRvMainList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

        mViewModel.mldDetail.observe(this, this::onDataListChanged)
        mViewModel.requestData(mPartnerId, mProductId, mCurationId)
    }

    private fun initActionBar() {
        val actionBar = ActionBar(this)
        actionBar.title = "DETAIL"
        actionBar.setButton(ActionBar.ACTION_BACK, ActionBar.ACTION_NONE)
        mBinding.actionbar = actionBar
    }

    private fun onDataListChanged(itemList: ProductDetail.Response.Data) {
        Trace.debug("++ onDataListChanged()")
        mAdapter.notifyDataSetChanged()
    }

    override fun onLeft() {
        finish()
    }

    override fun onRight() {
    }

    override fun onClick(v: View, pos: Int) {
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
    }
}