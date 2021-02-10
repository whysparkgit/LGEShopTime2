package com.lge.lgshoptimem.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.FragmentForyouBinding
import com.lge.lgshoptimem.model.dto.Curation
import com.lge.lgshoptimem.model.dto.ForYou
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.ComponentItemListener
import com.lge.lgshoptimem.ui.component.HeaderGridComponent
import com.lge.lgshoptimem.ui.component.HeaderListComponent
import com.lge.lgshoptimem.ui.product.DetailActivity

class ForYouFragment : Fragment(), ComponentItemListener {
    private lateinit var mBinding: FragmentForyouBinding
    private lateinit var mAdapter: ForYouListAdapter
    private val mViewModel: ForYouViewModel by viewModels()

    companion object {
        fun newInstance() = ForYouFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        mBinding = FragmentForyouBinding.inflate(inflater, container, false)
        mBinding.listener = this

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mAdapter = ForYouListAdapter(this)

        mBinding.fyRvMainList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

        mViewModel.mldForYou.observe(viewLifecycleOwner, this::onDataListChanged)
        mViewModel.requestData()
    }

    private fun onDataListChanged(itemList: ForYou.Response.Data) {
        Trace.debug("++ onDataListChanged()")
        mAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View, pos: Int) {
        Trace.debug("++ onClick() v = ${v.id} pos = $pos")

        when (mAdapter.getItemViewType(pos)) {
            AppConst.VIEWTYPE.VT_UPCOMING_HORIZONTAL -> Trace.debug(">> viewType = VT_NEXT_UPCOMING_HORIZONTAL")
            AppConst.VIEWTYPE.VT_FAVORITE_CATEGORY -> Trace.debug(">> viewType = VT_CATEGORY_REMINDER")
            AppConst.VIEWTYPE.VT_FAVORITE_KEYWORD -> Trace.debug(">> viewType = VT_KEYWORD_REMINDER")
            AppConst.VIEWTYPE.VT_MY_FAVORITES -> Trace.debug(">> viewType = VT_MY_FAVORITES")
            AppConst.VIEWTYPE.VT_RECENTLY_VIEWED -> Trace.debug(">> viewType = VT_RECENTLY_VIEWED")
            AppConst.VIEWTYPE.VT_COUPON -> Trace.debug(">> viewType = VT_COUPON")
            else -> Trace.debug(">> viewType = else")
        }

        // TODO remove
        when (v) {
            is HeaderListComponent -> Trace.debug(">> v = HeaderListComponent")
            is HeaderGridComponent -> Trace.debug(">> v = HeaderGridComponent")
            is ConstraintLayout -> Trace.debug(">> v = ConstraintLayout")
            else -> Trace.debug(">> v = else")
        }
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
        Trace.debug("++ onItemClick() parent = ${parent.id} parentPos = $parentPos item = ${item.id} pos = $pos")
        var strPartnerId = ""
        var strProductId = ""
        var strCurationId = ""

        when (mAdapter.getItemViewType(parentPos)) {
            AppConst.VIEWTYPE.VT_UPCOMING_HORIZONTAL -> Trace.debug(">> viewType = VT_NEXT_UPCOMING_HORIZONTAL")
            AppConst.VIEWTYPE.VT_FAVORITE_CATEGORY -> Trace.debug(">> viewType = VT_CATEGORY_REMINDER")
            AppConst.VIEWTYPE.VT_FAVORITE_KEYWORD -> Trace.debug(">> viewType = VT_KEYWORD_REMINDER")
            
            AppConst.VIEWTYPE.VT_MY_FAVORITES -> {
                strPartnerId = mViewModel.mldForYou.value!!.favorites[pos].patnrId
                strProductId = mViewModel.mldForYou.value!!.favorites[pos].prdtId
            }
            
            AppConst.VIEWTYPE.VT_RECENTLY_VIEWED -> Trace.debug(">> viewType = VT_RECENTLY_VIEWED")
            AppConst.VIEWTYPE.VT_COUPON -> Trace.debug(">> viewType = VT_COUPON")
            else -> Trace.debug(">> viewType = else")
        }

        if (strPartnerId.isNullOrEmpty() or strProductId.isNullOrEmpty()) return

        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(AppConst.KEY.PARTNER_ID, strPartnerId)
        intent.putExtra(AppConst.KEY.PRODUCT_ID, strProductId)
        intent.putExtra(AppConst.KEY.CURATION_ID, strCurationId)
        startActivity(intent)
    }
}