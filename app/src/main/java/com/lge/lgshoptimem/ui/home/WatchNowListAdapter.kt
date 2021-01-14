package com.lge.lgshoptimem.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.lge.core.app.ApplicationProxy
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.BR
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.*
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.ComponentItemListener
import com.lge.lgshoptimem.ui.component.HeaderListComponent

class WatchNowListAdapter(val mListener: ComponentItemListener):
        RecyclerView.Adapter<WatchNowListAdapter.ItemViewHolder<*>>()
{
    private val mViewModel = ApplicationProxy.getInstance().getActivity()!!.run {
        val viewModel: WatchNowViewModel by viewModels()
        viewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchNowListAdapter.ItemViewHolder<*> {
        Trace.debug("++ onCreateViewHolder() viewType = $viewType")
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            AppConst.VIEWTYPE.VT_PRODUCT_ITEM -> {
                val binding: ViewBroadcastProductsBinding = DataBindingUtil.inflate(inflater, R.layout.view_broadcast_products, parent, false)
                ItemViewHolder<ViewBroadcastProductsBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL -> {
                val binding: ViewNextUpcomingHorizontalBinding = DataBindingUtil.inflate(inflater, R.layout.view_next_upcoming_horizontal, parent, false)
                ItemViewHolder<ViewNextUpcomingHorizontalBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_TODAY_DEAL -> {
                val binding: ViewTodayDealsBinding = DataBindingUtil.inflate(inflater, R.layout.view_today_deals, parent, false)
                ItemViewHolder<ViewTodayDealsBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_PRODUCT_BC_ITEMS -> {
                val binding: ViewBroadcastItemsBinding = DataBindingUtil.inflate(inflater, R.layout.view_broadcast_items, parent, false)
                ItemViewHolder<ViewBroadcastItemsBinding>(binding.root)
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
        Trace.debug("++ onBindViewHolder()")
        val binding: ViewDataBinding = holder.getBinding() as ViewDataBinding
        binding.setVariable(BR.position, position)
        binding.setVariable(BR.listener, mListener)
        binding.setVariable(BR.viewmodel, mViewModel.mldDataList.value?.get(position))
        holder.bind()
    }

    override fun getItemCount(): Int {
        Trace.debug("++ getItemCount()")
        return getItemCount(AppConst.VIEWTYPE.VT_PRODUCT_ITEM) +
               getItemCount(AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL) +
               getItemCount(AppConst.VIEWTYPE.VT_TODAY_DEAL) +
               getItemCount(AppConst.VIEWTYPE.VT_PRODUCT_BC_ITEMS) +
               getItemCount(AppConst.VIEWTYPE.VT_YOU_MAY_LIKE)
    }

    fun getItemCount(viewType: Int): Int {
        Trace.debug("++ getItemCount(viewType)")
        return when (viewType) {
            AppConst.VIEWTYPE.VT_PRODUCT_ITEM -> 1
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL -> 1
            AppConst.VIEWTYPE.VT_TODAY_DEAL -> 1
            AppConst.VIEWTYPE.VT_PRODUCT_BC_ITEMS -> 5
            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE -> 1
            else -> 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        var nIndex: Int = 0;
        var nRet = AppConst.VIEWTYPE.VT_PRODUCT_ITEM

        if (position in nIndex until getItemCount(AppConst.VIEWTYPE.VT_PRODUCT_ITEM)) {
            nRet = AppConst.VIEWTYPE.VT_PRODUCT_ITEM
        }

        nIndex += getItemCount(AppConst.VIEWTYPE.VT_PRODUCT_ITEM)

        if (position in nIndex until nIndex + getItemCount(AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL)) {
            nRet = AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL
        }

        nIndex += getItemCount(AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL)

        if (position in nIndex until nIndex + getItemCount(AppConst.VIEWTYPE.VT_TODAY_DEAL)) {
            nRet = AppConst.VIEWTYPE.VT_TODAY_DEAL
        }
        nIndex += getItemCount(AppConst.VIEWTYPE.VT_TODAY_DEAL)

        if (position in nIndex until nIndex + getItemCount(AppConst.VIEWTYPE.VT_PRODUCT_BC_ITEMS)) {
            nRet = AppConst.VIEWTYPE.VT_PRODUCT_BC_ITEMS
        }

        nIndex += getItemCount(AppConst.VIEWTYPE.VT_PRODUCT_BC_ITEMS)

        if (position in nIndex until nIndex + getItemCount(AppConst.VIEWTYPE.VT_YOU_MAY_LIKE)) {
            nRet = AppConst.VIEWTYPE.VT_YOU_MAY_LIKE
        }


        Trace.debug("++ getItemViewType() position = $position nRet = $nRet")
        return nRet
    }

    inner class ItemViewHolder<B>(itemView: View) :
            RecyclerView.ViewHolder(itemView)
    {
        fun getBinding() = DataBindingUtil.getBinding<ViewDataBinding>(itemView) as B

        fun bind() {
//            itemView.findViewById<HeaderListComponent>(R.id.comp_list).addItemListener(mListener)

        }
    }
}