package com.lge.lgshoptimem.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.BR
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.FragmentHotpicksBinding
import com.lge.lgshoptimem.model.dto.HotPicks
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.ComponentItemListener
import com.lge.lgshoptimem.ui.product.DetailActivity

class HotPicksFragment : Fragment(), ComponentItemListener
{
    private lateinit var mBinding: FragmentHotpicksBinding
    private lateinit var mAdapter: HotPicksListAdapter
    private val mViewModel: HotPicksViewModel by viewModels()

    companion object {
        fun newInstance() = HotPicksFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        mBinding = FragmentHotpicksBinding.inflate(inflater, container, false)
        mBinding.listener = this

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mAdapter = HotPicksListAdapter(this)

        mBinding.hpRvMainList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

        mViewModel.mldHotPicks.observe(viewLifecycleOwner, this::onDataListChanged)
        mViewModel.requestData()
    }

    private fun onDataListChanged(itemList: HotPicks.Response.Data) {
        Trace.debug("++ onDataListChanged()")
        mAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View, pos: Int) {
        Trace.debug("++ onClick() v = ${v.id} pos = $pos")

        when (mAdapter.getItemViewType(pos)) {
            AppConst.VIEWTYPE.VT_TODAY_DEAL -> Trace.debug(">> viewType = VT_TODAY_DEAL")
            AppConst.VIEWTYPE.VT_HOT_PICKS -> Trace.debug(">> viewType = VT_HOT_PICKS")
            else -> Trace.debug(">> viewType = else")
        }
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
        Trace.debug("++ onItemClick() parent = ${parent.id} parentPos = $parentPos item = ${item.id} pos = $pos")
        var strPartnerId = ""
        var strProductId = ""
        var strCurationId = ""

        when (mAdapter.getItemViewType(parentPos)) {
            AppConst.VIEWTYPE.VT_TODAY_DEAL -> Trace.debug(">> viewType = VT_TODAY_DEAL")

            AppConst.VIEWTYPE.VT_HOT_PICKS -> {
                when (item.id) {
                    R.id.comp_iv_main_image -> Trace.debug(">> viewType = VT_HOT_PICKS")

                    else -> {
                        val nIndex: Int = mAdapter.getPositionOf(AppConst.VIEWTYPE.VT_HOT_PICKS, parentPos)
                        strPartnerId = mViewModel.mldHotPicks.value!!.themeInfos[nIndex].patnrId
                        strProductId = mViewModel.mldHotPicks.value!!.themeInfos[nIndex].productInfos[pos].prdtId
                        strCurationId = mViewModel.mldHotPicks.value!!.themeInfos[nIndex].curationId
                    }
                }
            }

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