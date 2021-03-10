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

    val marrViewTypes = arrayOf(
        AppConst.VIEWTYPE.VT_TODAY_DEAL,
        AppConst.VIEWTYPE.VT_HOT_PICKS
    )

    private val mListener: ComponentItemListener = mFragment as ComponentItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotPicksListAdapter.ItemViewHolder<*> {
//        Trace.debug("++ onCreateViewHolder() viewType = $viewType")
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            AppConst.VIEWTYPE.VT_TODAY_DEAL -> {
                val binding: ViewTodayDealsBinding = DataBindingUtil.inflate(inflater, R.layout.view_today_deals, parent, false)
                ItemViewHolder<ViewTodayDealsBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_HOT_PICKS -> {
                val binding: ViewHotPicksBinding = DataBindingUtil.inflate(inflater, R.layout.view_hot_picks, parent, false)
                ItemViewHolder<ViewHotPicksBinding>(binding.root)
            }

            else -> {
                // TODO not exit view
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

        if (mViewModel.mldHotPicks.value == null) return

        binding?.setVariable(BR.position, position)
        binding?.setVariable(BR.listener, mListener)

        val baseCompList = binding?.root?.findViewById<BaseListComponent>(R.id.comp_list)

        when (getItemViewType(position)) {
            AppConst.VIEWTYPE.VT_TODAY_DEAL -> {
//                baseCompList?.setHeadData(mViewModel.mldWatchNow.value!!.curations[position])
                baseCompList?.setItemList(mViewModel.mldHotPicks.value!!.curations)
            }

            AppConst.VIEWTYPE.VT_HOT_PICKS -> {
                val nIndex: Int = getPositionOf(AppConst.VIEWTYPE.VT_HOT_PICKS, position)
                binding?.setVariable(BR.index, nIndex)
                binding?.setVariable(BR.total_count, getItemCount(AppConst.VIEWTYPE.VT_HOT_PICKS))
//                binding?.setVariable(BR.viewdata, mViewModel.mldHotPicks.value!!.topInfos[nIndex])
                baseCompList?.setHeadData(mViewModel.mldHotPicks.value!!.themeInfos[nIndex])
                baseCompList?.setItemList(mViewModel.mldHotPicks.value!!.themeInfos[nIndex].productInfos)
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
//        Trace.debug("++ getItemCount(viewType)")
        if (mViewModel.mldHotPicks.value == null) return 0

        return when (viewType) {
            AppConst.VIEWTYPE.VT_TODAY_DEAL -> {
                return if (mViewModel.mldHotPicks.value!!.curations.isNullOrEmpty()) 0
                    else mViewModel.mldHotPicks.value!!.curations.size.let {
                        if (it > 0) 1 else it
                    }
            }

            AppConst.VIEWTYPE.VT_HOT_PICKS -> {
                return if (mViewModel.mldHotPicks.value!!.themeInfos.isNullOrEmpty()) 0
                    else mViewModel.mldHotPicks.value!!.themeInfos.size.let {
                        if (it >= 5) 5 else it
                    }
            }

            else -> 0
        }
    }

    override fun getItemViewType(position: Int): Int {
        var nIndex: Int = 0;

        marrViewTypes.forEach {
            nVal -> if (position in nIndex until nIndex + getItemCount(nVal)) {
                Trace.debug("++ getItemViewType() position = $position ViewType = $nVal")
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

    inner class ItemViewHolder<B>(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        fun getBinding(): ViewDataBinding? = DataBindingUtil.getBinding(itemView)

        fun setItemListener() {
            Trace.debug("++ bind()")
            val view: View = itemView.findViewById<ConstraintLayout>(R.id.comp_list)

            if (view is BaseListComponent) {
                Trace.debug(">> view is BaseListComponent")
                view.addItemListener(mListener)
            }
        }
    }
}