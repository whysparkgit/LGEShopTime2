package com.lge.lgshoptimem.ui.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityDetailBinding
import com.lge.lgshoptimem.databinding.FragmentDetailBinding
import com.lge.lgshoptimem.model.dto.ProductDetail
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.ComponentItemListener

class DetailFragment : Fragment(), ComponentItemListener
{
    private lateinit var mBinding: FragmentDetailBinding
    private lateinit var mAdapter: DetailListAdapter
    private val mViewModel: DetailViewModel by viewModels()

    private var mPartnerId: String = ""
    private var mProductId: String = ""
    private var mCurationId: String = ""
    private var mbFromPlayer: Boolean = false

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) {
            Trace.debug("++ newInstance()")

            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(AppConst.KEY.PARTNER_ID, mPartnerId)
                    putString(AppConst.KEY.PRODUCT_ID, mProductId)
                    putString(AppConst.KEY.CURATION_ID, mCurationId)
                    putBoolean(AppConst.KEY.LAUNCH_FROM, mbFromPlayer)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Trace.debug("++ onCreate()")
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPartnerId = it.getString(AppConst.KEY.PARTNER_ID).let { if (it.isNullOrEmpty()) "" else it }
            mProductId = it.getString(AppConst.KEY.PRODUCT_ID).let { if (it.isNullOrEmpty()) "" else it }
            mCurationId = it.getString(AppConst.KEY.CURATION_ID).let { if (it.isNullOrEmpty()) "" else it }
            mbFromPlayer = it.getBoolean(AppConst.KEY.LAUNCH_FROM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        Trace.debug("++ onCreateView()")
        mBinding = FragmentDetailBinding.inflate(inflater, container, false)
        mBinding.listener = this
        mBinding.clActionBar.visibility = if (mbFromPlayer) View.VISIBLE else View.GONE
        init()

        return mBinding.root
    }

    fun init() {
        if (mProductId.isNullOrEmpty()) return

        mAdapter = DetailListAdapter(this, !mbFromPlayer)

        mBinding.dtRvMainList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

        mViewModel.mldDetail.observe(viewLifecycleOwner, this::onDataListChanged)
        mViewModel.requestData(mPartnerId, mProductId, mCurationId)
    }

    private fun onDataListChanged(itemList: ProductDetail.Response.Data) {
        Trace.debug("++ onDataListChanged()")
        mAdapter.notifyDataSetChanged()
    }


    fun onClick(v: View) {
        when (v.id) {
            R.id.iv_close -> {
                activity?.onBackPressed()
            }
        }
    }

    override fun onClick(v: View, pos: Int) {
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
    }
}