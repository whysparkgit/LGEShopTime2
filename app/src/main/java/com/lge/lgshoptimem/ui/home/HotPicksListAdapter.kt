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
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.BaseListComponent
import com.lge.lgshoptimem.ui.component.ComponentItemListener

class HotPicksListAdapter(mFragment: Fragment):
        RecyclerView.Adapter<HotPicksListAdapter.ItemViewHolder<*>>()
{
    private val mViewModel = mFragment.run {
        val viewModel: HotPicksViewModel by viewModels()
        viewModel
    }

    private val mListener: ComponentItemListener = mFragment as ComponentItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotPicksListAdapter.ItemViewHolder<*> {
//        Trace.debug("++ onCreateViewHolder() viewType = $viewType")
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> {
                val binding: ViewProductListBinding = DataBindingUtil.inflate(inflater, R.layout.view_product_list, parent, false)
                ItemViewHolder<ViewProductListBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL -> {
                val binding: ViewNextUpcomingHorizontalBinding = DataBindingUtil.inflate(inflater, R.layout.view_next_upcoming_horizontal, parent, false)
                ItemViewHolder<ViewNextUpcomingHorizontalBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_TODAY_DEAL -> {
                val binding: ViewTodayDealsBinding = DataBindingUtil.inflate(inflater, R.layout.view_today_deals, parent, false)
                ItemViewHolder<ViewTodayDealsBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_HOT_PICKS -> {
                val binding: ViewHotPicksBinding = DataBindingUtil.inflate(inflater, R.layout.view_hot_picks, parent, false)
                ItemViewHolder<ViewHotPicksBinding>(binding.root)
            }

            else -> {
                val binding: ViewProductListBinding = DataBindingUtil.inflate(inflater, R.layout.view_product_list, parent, false)
                ItemViewHolder<ViewProductListBinding>(binding.root)
            }
        }
    }

    override fun onBindViewHolder(holder: HotPicksListAdapter.ItemViewHolder<*>, position: Int) {
//        Trace.debug("++ onBindViewHolder()")
        val binding: ViewDataBinding? = holder.getBinding()
        binding?.setVariable(BR.position, position)
        binding?.setVariable(BR.listener, mListener)

        if (mViewModel.mldDataList.value.isNullOrEmpty()) return

        val baseCompList = binding?.root?.findViewById<BaseListComponent>(R.id.comp_list)

        when (getItemViewType(position)) {
            AppConst.VIEWTYPE.VT_TODAY_DEAL -> {
                baseCompList?.setHeadData(mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size])
                baseCompList?.setItemList(mViewModel.mldDataList.value!!)
            }

            else -> {
                baseCompList?.setHeadData(mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size])
                baseCompList?.setItemList(mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size].productInfos)
            }
        }

        holder.bind()
    }

    override fun getItemCount(): Int {
        var nCount = 0

        var arrViewTypes = arrayOf(
                AppConst.VIEWTYPE.VT_LIVE_CHANNELS,
                AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL,
                AppConst.VIEWTYPE.VT_TODAY_DEAL,
                AppConst.VIEWTYPE.VT_HOT_PICKS
        )

        arrViewTypes.forEach {
            nVal -> nCount += getItemCount(nVal)
        }

//        Trace.debug("++ getItemCount() nCount = $nCount")
        return nCount
    }

    fun getItemCount(viewType: Int): Int {
//        Trace.debug("++ getItemCount(viewType)")
        return when (viewType) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> 0
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL -> 0
            AppConst.VIEWTYPE.VT_TODAY_DEAL -> 1
            AppConst.VIEWTYPE.VT_HOT_PICKS -> 5
            else -> 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        var nIndex: Int = 0;

        var arrViewTypes = arrayOf(
                AppConst.VIEWTYPE.VT_LIVE_CHANNELS,
                AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL,
                AppConst.VIEWTYPE.VT_TODAY_DEAL,
                AppConst.VIEWTYPE.VT_HOT_PICKS
        )

        arrViewTypes.forEach {
            nVal -> if (position in nIndex until nIndex + getItemCount(nVal)) {
                Trace.debug("++ getItemViewType() position = $position ViewType = $nVal")
                return nVal
            }

            nIndex += getItemCount(nVal)
        }

        return arrViewTypes.last()
    }

    inner class ItemViewHolder<B>(itemView: View) :
            RecyclerView.ViewHolder(itemView)
    {
        fun getBinding(): ViewDataBinding? = DataBindingUtil.getBinding(itemView)

        fun bind() {
            Trace.debug("++ bind()")
            val view: View = itemView.findViewById<ConstraintLayout>(R.id.comp_list)

            if (view is BaseListComponent) {
                Trace.debug(">> view is BaseListComponent")
                view.addItemListener(mListener)
            }
        }
    }
}