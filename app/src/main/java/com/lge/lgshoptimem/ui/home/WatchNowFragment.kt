package com.lge.lgshoptimem.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.FragmentWatchnowBinding
import com.lge.lgshoptimem.databinding.ViewLiveChannelIconsBinding
import com.lge.lgshoptimem.databinding.ViewLiveChannelsBinding
import com.lge.lgshoptimem.model.dto.WatchNow
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.BaseListComponent
import com.lge.lgshoptimem.ui.component.ComponentItemListener
import com.lge.lgshoptimem.ui.product.DetailActivity

class WatchNowFragment : Fragment(), ComponentItemListener {
    private lateinit var mBinding: FragmentWatchnowBinding
    private lateinit var mAdapter: WatchNowListAdapter
    private val mViewModel: WatchNowViewModel by viewModels()

    companion object {
        fun newInstance() = WatchNowFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        Trace.debug("++ onCreateView()")
        mBinding = FragmentWatchnowBinding.inflate(inflater, container, false)
        mBinding.listener = this

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Trace.debug("++ onActivityCreated()")

        mAdapter = WatchNowListAdapter(this)

        mBinding.wnRvMainList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

        mViewModel.mldWatchNow.observe(viewLifecycleOwner, this::onDataListChanged)
//        mViewModel.requestData("", "")
        mViewModel.requestData("1", "QVC20210202000000")
    }

    private fun onDataListChanged(itemList: WatchNow.Response.Data) {
        Trace.debug("++ onDataListChanged()")
        mAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View, pos: Int) {
        Trace.debug("++ onClick() v = ${v.id} pos = $pos")

        when (mAdapter.getItemViewType(pos)) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> {
                Trace.debug(">> viewType = VT_LIVE_CHANNEL_PRODUCT")

                when (v.id) {
                    R.id.comp_tv_more -> {
                        val itemBinding: ViewDataBinding? = (mBinding.wnRvMainList.findViewHolderForAdapterPosition(pos) as WatchNowListAdapter.ItemViewHolder<*>).getBinding()
                        var nCount = itemBinding?.root?.findViewById<BaseListComponent>(R.id.comp_list)?.mAdapter?.itemCount

                        Trace.debug(">> mAdapter.itemCount = $nCount")
                        Trace.debug(">> productInfos.size = ${mViewModel.mldWatchNow.value!!.productInfos.size}")

                        if (mViewModel.mldWatchNow.value!!.productInfos.size > nCount!! + 10) {
                            itemBinding?.root?.findViewById<BaseListComponent>(R.id.comp_list)?.setItemCountLimit(nCount + 10)
                        } else if (mViewModel.mldWatchNow.value!!.productInfos.size > nCount) {
                            nCount = mViewModel.mldWatchNow.value!!.productInfos.size
                            itemBinding?.root?.findViewById<BaseListComponent>(R.id.comp_list)?.setItemCountLimit(nCount)
                        }

                        itemBinding?.root?.findViewById<BaseListComponent>(R.id.comp_list)?.refresh()
                        mAdapter.notifyDataSetChanged()
                    }
                }
            }
            AppConst.VIEWTYPE.VT_UPCOMING_HORIZONTAL -> Trace.debug(">> viewType = VT_NEXT_UPCOMING_HORIZONTAL")
            AppConst.VIEWTYPE.VT_TODAY_DEAL -> Trace.debug(">> viewType = VT_TODAY_DEAL")
            AppConst.VIEWTYPE.VT_POPULAR_SHOWS -> Trace.debug(">> viewType = VT_POPULAR_SHOWS")
            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE -> Trace.debug(">> viewType = VT_YOU_MAY_LIKE")
            else -> Trace.debug(">> viewType = else")
        }
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
        Trace.debug("++ onItemClick() parent = ${parent.id} parentPos = $parentPos item = ${item.id} pos = $pos")
        var strPartnerId = ""
        var strProductId = ""
        var strCurationId = ""

        when (mAdapter.getItemViewType(parentPos)) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {
                if (mAdapter.mShowInfoIndex == pos) return

                mAdapter.mChannelIcons.forEach { it.selected = false }
                mAdapter.mChannelIcons[pos].selected = true
                mAdapter.mShowInfoIndex = pos
                mBinding.wnRvMainList.getChildAt(parentPos).findViewById<BaseListComponent>(R.id.comp_list).refresh()

                mViewModel.requestData(mViewModel.mldWatchNow.value!!.showInfos[pos].patnrId,
                        mViewModel.mldWatchNow.value!!.showInfos[pos].showId)
            }

            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> {
                strPartnerId = mViewModel.mldWatchNow.value!!.productInfos[pos].patnrId
                strProductId = mViewModel.mldWatchNow.value!!.productInfos[pos].prdtId
                strCurationId = mViewModel.mldWatchNow.value!!.productInfos[pos].curationId
            }

            AppConst.VIEWTYPE.VT_UPCOMING_HORIZONTAL -> Trace.debug(">> viewType = VT_NEXT_UPCOMING_HORIZONTAL")

            AppConst.VIEWTYPE.VT_TODAY_DEAL -> Trace.debug(">> viewType = VT_TODAY_DEAL")

            AppConst.VIEWTYPE.VT_POPULAR_SHOWS -> {
                val nIndex: Int = mAdapter.getPositionOf(AppConst.VIEWTYPE.VT_POPULAR_SHOWS, parentPos)
                strPartnerId = mViewModel.mldWatchNow.value!!.topInfos[nIndex].patnrId
                strProductId = mViewModel.mldWatchNow.value!!.topInfos[nIndex].productInfos[pos].prdtId
            }

            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE -> {
                strPartnerId = mViewModel.mldWatchNow.value!!.youmaylike[pos].patnrId
                strProductId = mViewModel.mldWatchNow.value!!.youmaylike[pos].prdtId
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

    override fun onDestroyView() {
        Trace.debug("++ onDestroyView()")
        super.onDestroyView()
    }
}