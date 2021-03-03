package com.lge.lgshoptimem.ui.home

import android.app.Activity
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
import com.lge.lgshoptimem.model.dto.Schedule
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.BaseListComponent
import com.lge.lgshoptimem.ui.component.ComponentItemListener

class ScheduleListAdapter(mActivity:Activity):
        RecyclerView.Adapter<ScheduleListAdapter.ItemViewHolder<*>>()
{
//    private val mViewModel = mActivity.run {
//        val viewModel: WatchNowViewModel by viewModels()
//        viewModel
//    }

    val marrViewTypes = arrayOf(
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS,
//            AppConst.VIEWTYPE.VT_LIVE_CHANNELS,
//            AppConst.VIEWTYPE.VT_UPCOMING_HORIZONTAL,
//            AppConst.VIEWTYPE.VT_TODAY_DEAL,
//            AppConst.VIEWTYPE.VT_POPULAR_SHOWS,
//            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE
    )

    private val mListener: ComponentItemListener = mActivity as ComponentItemListener
    var mShowInfoIndex = 0
    val mSchedules: ArrayList<Schedule> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleListAdapter.ItemViewHolder<*> {
        Trace.debug("++ onCreateViewHolder() viewType = $viewType")
        val inflater = LayoutInflater.from(parent.context)


        return when (viewType) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {
                val binding: ViewScheduleItemsBinding = DataBindingUtil.inflate(inflater, R.layout.view_schedule_items, parent, false)
                ItemViewHolder<ViewScheduleItemsBinding>(binding.root)
            }

            else -> {
                // TODO not exit view
                val binding: ViewProductListBinding = DataBindingUtil.inflate(inflater, R.layout.view_product_list, parent, false)
                ItemViewHolder<ViewProductListBinding>(binding.root)
            }
        }
    }

    override fun onBindViewHolder(holder: ScheduleListAdapter.ItemViewHolder<*>, position: Int) {
        val binding: ViewDataBinding? = holder.getBinding()
        Trace.debug("++ onBindViewHolder() position = $position binding = $binding")

//        if (mViewModel.mldWatchNow.value == null) return

        binding?.setVariable(BR.position, position)
        binding?.setVariable(BR.listener, mListener)

        val baseCompList = binding?.root?.findViewById<BaseListComponent>(R.id.comp_list)

        when (getItemViewType(position)) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {
                if (mSchedules.size == 0) {
//                if(true) {
                    //선택, 요일, 날짜
                    mSchedules.add(Schedule(false,"-1-","1"))
                    mSchedules.add(Schedule(false,"-2-","2"))
                    mSchedules.add(Schedule(false,"-3-","3"))
                    mSchedules.add(Schedule(false,"-4-","4"))
                    mSchedules.add(Schedule(false,"-5-","5"))
                    mSchedules.add(Schedule(false,"-6-","6"))
                    mSchedules.add(Schedule(false,"-7-","7"))
                } else {
                    mSchedules.forEach { it.selected = false }
                }

                mSchedules[mShowInfoIndex].selected = true
                baseCompList?.setItemList(mSchedules)
//                baseCompList?.mBinding?.root?.findViewById<RecyclerView>(R.id.comp_rv_list)!!.setItemViewCacheSize(10)
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

        Trace.debug("++ getItemCount() nCount = $nCount")
        return nCount
//        return 1
    }

    fun getItemCount(viewType: Int): Int {
        Trace.debug("++ getItemCount() viewType = $viewType")
//
//        if (mViewModel.mldWatchNow.value == null) return 0

//        return when (viewType) {
//            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {        // showInfos
//                if (mViewModel.mldWatchNow.value!!.showInfos.size > 0) 1 else 0
//            }

//            else -> 0
//        }
//        return 0
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        var nIndex: Int = 0

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