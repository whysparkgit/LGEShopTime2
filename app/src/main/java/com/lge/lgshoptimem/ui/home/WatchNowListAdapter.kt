package com.lge.lgshoptimem.ui.home

import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.lge.core.app.ApplicationProxy
import com.lge.core.sys.Trace
import com.lge.core.sys.Util.Companion.toIntArrayList
import com.lge.lgshoptimem.BR
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.*
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.*

class WatchNowListAdapter(mFragment: Fragment):
        RecyclerView.Adapter<WatchNowListAdapter.ItemViewHolder<*>>()
{
    private val mViewModel = mFragment.run {
        val viewModel: WatchNowViewModel by viewModels()
        viewModel
    }

    private val mListener: ComponentItemListener = mFragment as ComponentItemListener

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

            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL -> {
                val binding: ViewNextUpcomingHorizontalBinding = DataBindingUtil.inflate(inflater, R.layout.view_next_upcoming_horizontal, parent, false)
                ItemViewHolder<ViewNextUpcomingHorizontalBinding>(binding.root)
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
                val binding: ViewProductListBinding = DataBindingUtil.inflate(inflater, R.layout.view_product_list, parent, false)
                ItemViewHolder<ViewProductListBinding>(binding.root)
            }
        }
    }

    override fun onBindViewHolder(holder: WatchNowListAdapter.ItemViewHolder<*>, position: Int) {
        val binding: ViewDataBinding? = holder.getBinding()
        Trace.debug("++ onBindViewHolder() position = $position binding = $binding")
        binding?.setVariable(BR.position, position)
        binding?.setVariable(BR.listener, mListener)

        val baseCompList = binding?.root?.findViewById<BaseListComponent>(R.id.comp_list)

        when (getItemViewType(position)) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {
                val resources: TypedArray = ApplicationProxy.getContext().resources.obtainTypedArray(R.array.id_channel_icons)
                baseCompList?.setItemList(resources.toIntArrayList())
                resources.recycle()
            }

            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> {
                if (!mViewModel.mldDataList.value.isNullOrEmpty()) {
                    binding?.setVariable(BR.viewdata, mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size])
                    baseCompList?.setHeadData(mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size])
                    baseCompList?.setItemList(mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size].productInfos)
                }
            }

            AppConst.VIEWTYPE.VT_TODAY_DEAL -> {
                if (!mViewModel.mldDataList.value.isNullOrEmpty()) {
                    baseCompList?.setHeadData(mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size])
                    baseCompList?.setItemList(mViewModel.mldDataList.value!!)
                }
            }

            AppConst.VIEWTYPE.VT_POPULAR_SHOWS -> {
                if (!mViewModel.mldDataList.value.isNullOrEmpty()) {
                    binding?.setVariable(BR.viewdata, mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size])
                    baseCompList?.setHeadData(mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size])
                    baseCompList?.setItemList(mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size].productInfos)
                }
            }

            else -> {
                if (!mViewModel.mldDataList.value.isNullOrEmpty()) {
                    baseCompList?.setHeadData(mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size])
                    baseCompList?.setItemList(mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size].productInfos)
                }
            }
        }

        holder.bind()
    }

    override fun getItemCount(): Int {
        var nCount = 0

        var arrViewTypes = arrayOf(
                AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS,
                AppConst.VIEWTYPE.VT_LIVE_CHANNELS,
                AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL,
                AppConst.VIEWTYPE.VT_TODAY_DEAL,
                AppConst.VIEWTYPE.VT_POPULAR_SHOWS,
                AppConst.VIEWTYPE.VT_YOU_MAY_LIKE
        )

        arrViewTypes.forEach {
            nVal -> nCount += getItemCount(nVal)
        }

//        Trace.debug("++ getItemCount() nCount = $nCount")
        return nCount
    }

    fun getItemCount(viewType: Int): Int {
//        Trace.debug("++ getItemCount() viewType = $viewType")
        return when (viewType) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> 1
            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> 1
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL -> 1
            AppConst.VIEWTYPE.VT_TODAY_DEAL -> 1
            AppConst.VIEWTYPE.VT_POPULAR_SHOWS -> 5
            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE -> 1
            else -> 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        var nIndex: Int = 0

        var arrViewTypes = arrayOf(
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS,
            AppConst.VIEWTYPE.VT_LIVE_CHANNELS,
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL,
            AppConst.VIEWTYPE.VT_TODAY_DEAL,
            AppConst.VIEWTYPE.VT_POPULAR_SHOWS,
            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE
        )

        arrViewTypes.forEach {
            nVal -> if (position in nIndex until nIndex + getItemCount(nVal)) {
//                Trace.debug("++ getItemViewType() position = $position ViewType = $nVal")
                return nVal
            }

            nIndex += getItemCount(nVal)
        }

        return arrViewTypes.last()
    }

    fun getViewTypeString(position: Int): String {
        var nType: Int = getItemViewType(position);

        var arrViewTypes = arrayOf(
                AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS,
                AppConst.VIEWTYPE.VT_LIVE_CHANNELS,
                AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL,
                AppConst.VIEWTYPE.VT_TODAY_DEAL,
                AppConst.VIEWTYPE.VT_POPULAR_SHOWS,
                AppConst.VIEWTYPE.VT_YOU_MAY_LIKE
        )

        var strViewTypes = arrayOf(
                "VT_LIVE_CHANNEL_ICONS",
                "VT_LIVE_CHANNEL_PRODUCT",
                "VT_NEXT_UPCOMING_HORIZONTAL",
                "VT_TODAY_DEAL",
                "VT_POPULAR_SHOWS_PRODUCT",
                "VT_YOU_MAY_LIKE"
        )

        arrViewTypes.forEachIndexed { index, nVal -> if (nVal == nType) return strViewTypes[index] }

        return ""
    }

    inner class ItemViewHolder<B>(itemView: View) :
            RecyclerView.ViewHolder(itemView)
    {
        fun getBinding() = DataBindingUtil.getBinding<ViewDataBinding>(itemView)

        fun bind() {
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