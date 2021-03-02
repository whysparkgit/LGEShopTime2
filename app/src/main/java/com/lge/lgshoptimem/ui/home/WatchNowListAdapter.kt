package com.lge.lgshoptimem.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.BR
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.*
import com.lge.lgshoptimem.model.dto.ChannelIcon
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.BaseListComponent
import com.lge.lgshoptimem.ui.component.ComponentItemListener

class WatchNowListAdapter(mFragment: Fragment):
        RecyclerView.Adapter<WatchNowListAdapter.ItemViewHolder<*>>()
{
    private val mViewModel = mFragment.run {
        val viewModel: WatchNowViewModel by viewModels()
        viewModel
    }

    val marrViewTypes = arrayOf(
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS,
            AppConst.VIEWTYPE.VT_LIVE_CHANNELS,
            AppConst.VIEWTYPE.VT_UPCOMING_HORIZONTAL,
            AppConst.VIEWTYPE.VT_TODAY_DEAL,
            AppConst.VIEWTYPE.VT_POPULAR_SHOWS,
            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE
    )

    private val mListener: ComponentItemListener = mFragment as ComponentItemListener
    var mShowInfoIndex = 0
    val mChannelIcons: ArrayList<ChannelIcon> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchNowListAdapter.ItemViewHolder<*> {
//        Trace.debug("++ onCreateViewHolder() viewType = $viewType")
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {
                val binding: ViewLiveChannelIconsBinding = DataBindingUtil.inflate(inflater, R.layout.view_live_channel_icons, parent, false)
                ItemViewHolder<ViewLiveChannelIconsBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> {
                val binding: ViewLiveChannelsBinding = DataBindingUtil.inflate(inflater, R.layout.view_live_channels, parent, false)
                ItemViewHolder<ViewLiveChannelsBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_UPCOMING_HORIZONTAL -> {
                val binding: ViewUpcomingHorizontalBinding = DataBindingUtil.inflate(inflater, R.layout.view_upcoming_horizontal, parent, false)
                ItemViewHolder<ViewUpcomingHorizontalBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_TODAY_DEAL -> {
                val binding: ViewTodayDealsBinding = DataBindingUtil.inflate(inflater, R.layout.view_today_deals, parent, false)
                ItemViewHolder<ViewTodayDealsBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_POPULAR_SHOWS -> {
                val binding: ViewPopularShowsBinding = DataBindingUtil.inflate(inflater, R.layout.view_popular_shows, parent, false)
                ItemViewHolder<ViewPopularShowsBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE -> {
                val binding: ViewYouMayLikeBinding = DataBindingUtil.inflate(inflater, R.layout.view_you_may_like, parent, false)
                ItemViewHolder<ViewYouMayLikeBinding>(binding.root)
            }

            else -> {
                // TODO not exit view
                val binding: ViewProductListBinding = DataBindingUtil.inflate(inflater, R.layout.view_product_list, parent, false)
                ItemViewHolder<ViewProductListBinding>(binding.root)
            }
        }
    }

    override fun onBindViewHolder(holder: WatchNowListAdapter.ItemViewHolder<*>, position: Int) {
        val binding: ViewDataBinding? = holder.getBinding()
        Trace.debug("++ onBindViewHolder() position = $position binding = $binding")

        if (mViewModel.mldWatchNow.value == null) return

        binding?.setVariable(BR.position, position)
        binding?.setVariable(BR.listener, mListener)

        val baseCompList = binding?.root?.findViewById<BaseListComponent>(R.id.comp_list)

        when (getItemViewType(position)) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {
                if (mChannelIcons.size == 0) {
                    mViewModel.mldWatchNow.value!!.showInfos.forEach {
                        when (it.patnrId.toInt()) {
                            1 -> { mChannelIcons.add(ChannelIcon(false, R.drawable.sel_channel_qvc, "")) }     // QVC
                            2 -> { mChannelIcons.add(ChannelIcon(false, R.drawable.sel_channel_hsn, "")) }     // HSN
                            3 -> { mChannelIcons.add(ChannelIcon(false, R.drawable.sel_channel_jtv, "")) }     // JTV
                            4 -> { mChannelIcons.add(ChannelIcon(false, R.drawable.sel_channel_ontv, "")) }    // onTV
                        }
                    }
                } else {
                    mChannelIcons.forEach { it.selected = false }
                }

                mChannelIcons[mShowInfoIndex].selected = true
                baseCompList?.setItemList(mChannelIcons)
            }

            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> {
                binding?.setVariable(BR.viewdata, mViewModel.mldWatchNow.value!!.showInfos[mShowInfoIndex])
                baseCompList?.setItemList(mViewModel.mldWatchNow.value!!.productInfos)
                binding?.setVariable(BR.current_count, baseCompList?.mAdapter?.itemCount)
                binding?.setVariable(BR.total_count, mViewModel.mldWatchNow.value!!.productInfos.size)
            }

            AppConst.VIEWTYPE.VT_UPCOMING_HORIZONTAL -> {
                baseCompList?.setItemList(mViewModel.mldWatchNow.value!!.upcomingItems)
            }

            AppConst.VIEWTYPE.VT_TODAY_DEAL -> {
                baseCompList?.setItemList(mViewModel.mldWatchNow.value!!.curations)
            }

            AppConst.VIEWTYPE.VT_POPULAR_SHOWS -> {
                val nIndex: Int = getPositionOf(AppConst.VIEWTYPE.VT_POPULAR_SHOWS, position)
                binding?.setVariable(BR.index, nIndex)
                binding?.setVariable(BR.total_count, getItemCount(AppConst.VIEWTYPE.VT_POPULAR_SHOWS))
                binding?.setVariable(BR.viewdata, mViewModel.mldWatchNow.value!!.topInfos[nIndex])
                baseCompList?.setItemList(mViewModel.mldWatchNow.value!!.topInfos[nIndex].productInfos)
            }

            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE -> {
                baseCompList?.setItemList(mViewModel.mldWatchNow.value!!.youmaylike)
            }

            else -> { }
        }

        holder.setItemListener()
    }

    override fun getItemCount(): Int {
        var nCount = 0

        marrViewTypes.forEach {
            nVal -> nCount += getItemCount(nVal)
        }

//        Trace.debug("++ getItemCount() nCount = $nCount")
        return nCount
    }

    fun getItemCount(viewType: Int): Int {
//        Trace.debug("++ getItemCount() viewType = $viewType")

        if (mViewModel.mldWatchNow.value == null) return 0

        return when (viewType) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {        // showInfos
                if (mViewModel.mldWatchNow.value!!.showInfos.size > 0) 1 else 0
            }

            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> {             // showInfos + productInfos(10)
                if (mViewModel.mldWatchNow.value!!.showInfos.size > 0) 1 else 0
            }

            AppConst.VIEWTYPE.VT_UPCOMING_HORIZONTAL -> {       // upcoming(1)
                if (mViewModel.mldWatchNow.value!!.upcomingItems.size > 0) 1 else 0
            }

            AppConst.VIEWTYPE.VT_TODAY_DEAL -> {                // curation
                if (mViewModel.mldWatchNow.value!!.curations.size > 0) 1 else 0
            }

            AppConst.VIEWTYPE.VT_POPULAR_SHOWS -> {             // topInfos(5)
                val nCount = mViewModel.mldWatchNow.value!!.topInfos.size
                if (nCount >= 5) 5 else nCount
            }

            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE -> {              // youMayLike(10)
                if (mViewModel.mldWatchNow.value!!.youmaylike.size > 0) 1 else 0
            }

            else -> 0
        }
    }

    override fun getItemViewType(position: Int): Int {
        var nIndex: Int = 0

        marrViewTypes.forEach {
            nVal -> if (position in nIndex until nIndex + getItemCount(nVal)) {
//                Trace.debug("++ getItemViewType() position = $position ViewType = $nVal")
                return nVal
            }

            nIndex += getItemCount(nVal)
        }

        return marrViewTypes.last()
    }

    fun getPositionOf(viewType: Int, position: Int): Int {
        var nCount: Int = 0

        marrViewTypes.forEach {
            nVal -> if (nVal == viewType) {
                Trace.debug("++ getPositionOf() result = ${position - nCount}")
                return (position - nCount).let {
                    if (it < 0) 0
                    else it
                }
            } else {
                nCount += getItemCount(nVal)
            }
        }

        return 0
    }

    fun getViewTypeString(position: Int): String {
        val nType: Int = getItemViewType(position);

        val strViewTypes = arrayOf(
                "VT_LIVE_CHANNEL_ICONS",
                "VT_LIVE_CHANNEL_PRODUCT",
                "VT_NEXT_UPCOMING_HORIZONTAL",
                "VT_TODAY_DEAL",
                "VT_POPULAR_SHOWS_PRODUCT",
                "VT_YOU_MAY_LIKE"
        )

        marrViewTypes.forEachIndexed { index, nVal -> if (nVal == nType) return strViewTypes[index] }

        return ""
    }

    inner class ItemViewHolder<B>(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        fun getBinding() = DataBindingUtil.getBinding<ViewDataBinding>(itemView)

        fun setItemListener() {
            Trace.debug("++ bind()")
            val view: View? = itemView.findViewById<ConstraintLayout>(R.id.comp_list)

            view?.apply {
                if (this is BaseListComponent) {
                    Trace.debug(">> view is BaseListComponent")
                    addItemListener(mListener)
                }
            }
        }
    }
}