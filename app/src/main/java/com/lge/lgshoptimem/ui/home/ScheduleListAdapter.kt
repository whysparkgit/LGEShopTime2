package com.lge.lgshoptimem.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.lge.core.sys.Trace
import com.lge.core.sys.Util.Companion.GetNowDateFormat
import com.lge.lgshoptimem.BR
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.*
import com.lge.lgshoptimem.model.dto.ChannelIcon
import com.lge.lgshoptimem.model.dto.ScheduleDate
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.BaseListComponent
import com.lge.lgshoptimem.ui.component.ComponentItemListener
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ScheduleListAdapter(mActivity: ScheduleActivity):
        RecyclerView.Adapter<ScheduleListAdapter.ItemViewHolder<*>>()
{
    private val mViewModel = mActivity.run {
        val viewModel: ScheduleViewModel by viewModels()
        viewModel
    }

    val marrViewTypes = arrayOf(
            AppConst.VIEWTYPE.VT_SCHEDULE_DATE,
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS,
            AppConst.VIEWTYPE.VT_UPCOMING_VERTICAL
    )

    private val mListener: ComponentItemListener = mActivity as ComponentItemListener
    var mScheduleShowIndex = 0
    var mChannelShowIndex = 0
    val mScheduleDates: ArrayList<ScheduleDate> = arrayListOf()
    val mChannelIcons: ArrayList<ChannelIcon> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleListAdapter.ItemViewHolder<*> {
        Trace.debug("++ onCreateViewHolder() viewType = $viewType")
        val inflater = LayoutInflater.from(parent.context)


        return when (viewType) {
            AppConst.VIEWTYPE.VT_SCHEDULE_DATE -> {
                val binding: ViewScheduleItemsBinding = DataBindingUtil.inflate(inflater, R.layout.view_schedule_items, parent, false)
                binding.root.findViewById<BaseListComponent>(R.id.comp_list)?.setTitle(getDate(10))
                ItemViewHolder<ViewScheduleItemsBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {
                val binding: ViewLiveChannelIconsBinding = DataBindingUtil.inflate(inflater, R.layout.view_live_channel_icons, parent, false)
                ItemViewHolder<ViewLiveChannelIconsBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_UPCOMING_VERTICAL -> {
                val binding: ViewUpcomingVerticalBinding = DataBindingUtil.inflate(inflater, R.layout.view_upcoming_vertical, parent, false)
                ItemViewHolder<ViewUpcomingVerticalBinding>(binding.root)
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

        if (mViewModel.mldSchedule.value == null) return

        binding?.setVariable(BR.position, position)
        binding?.setVariable(BR.listener, mListener)

        val baseCompList = binding?.root?.findViewById<BaseListComponent>(R.id.comp_list)

        when (getItemViewType(position)) {
            AppConst.VIEWTYPE.VT_SCHEDULE_DATE -> {
                if (mScheduleDates.size == 0) {
                    mScheduleDates.add(ScheduleDate(false,getConvertedDate(0),getDate(0)))
                    mScheduleDates.add(ScheduleDate(false,getConvertedDate(1),getDate(1)))
                    mScheduleDates.add(ScheduleDate(false,getConvertedDate(2),getDate(2)))
                    mScheduleDates.add(ScheduleDate(false,getConvertedDate(3),getDate(3)))
                    mScheduleDates.add(ScheduleDate(false,getConvertedDate(4),getDate(4)))
                    mScheduleDates.add(ScheduleDate(false,getConvertedDate(5),getDate(5)))
                    mScheduleDates.add(ScheduleDate(false,getConvertedDate(6),getDate(6)))
                } else {
                    mScheduleDates.forEach { it.selected = false }
                }

                mScheduleDates[mScheduleShowIndex].selected = true
                baseCompList?.setItemList(mScheduleDates)
            }

            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {
                if (mChannelIcons.size == 0) {
                    mViewModel.mldSchedule.value!!.partnerInfos.forEach {
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

                mChannelIcons[mChannelShowIndex].selected = true
                baseCompList?.setItemList(mChannelIcons)
            }

            AppConst.VIEWTYPE.VT_UPCOMING_VERTICAL -> {
                baseCompList?.setItemList(mViewModel.mldSchedule.value!!.upcomingItems)
            }

            else -> { }
        }

        holder.setItemListener()
    }

    private fun getDate(indicator: Int): String {

        when {
            indicator == 10 -> {
//                Trace.debug(">>>>>>> time " + System.currentTimeMillis().toDateFormat("yyyy"))
                val year = GetNowDateFormat("yyyy")
                val cal = Calendar.getInstance()
                val month = (cal.get(Calendar.MONTH) + 1)

                return "$year ${convertMonth(month)}"
            }

            indicator != 10 -> {
                val cal = Calendar.getInstance()

                cal.time = Date()
                val df: DateFormat = SimpleDateFormat("dd")
                cal.add(Calendar.DATE, + indicator)

                return df.format(cal.time)
            }

            else -> {
                return ""
            }
        }
    }

    private fun getConvertedDate(indicator: Int) : String {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, +indicator)

        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            1 -> "SUN"
            2 -> "MON"
            3 -> "TUE"
            4 -> "WED"
            5 -> "THR"
            6 -> "FRI"
            7 -> "SAT"
            else -> "---"
        }
    }

    private fun convertMonth(month: Int): String {
        return when(month) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> ""
        }
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
        Trace.debug("++ getItemCount() viewType = $viewType")

        if (mViewModel.mldSchedule.value == null) return 0

        return when (viewType) {
            AppConst.VIEWTYPE.VT_SCHEDULE_DATE -> 1

            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {        // showInfos
                return if (mViewModel.mldSchedule.value!!.partnerInfos.isNullOrEmpty()) 0
                else mViewModel.mldSchedule.value!!.partnerInfos.size.let {
                    if (it > 0) 1 else it
                }
            }

            AppConst.VIEWTYPE.VT_UPCOMING_VERTICAL -> {       // upcoming(1)
                return if (mViewModel.mldSchedule.value!!.upcomingItems.isNullOrEmpty()) 0
                else mViewModel.mldSchedule.value!!.upcomingItems.size.let {
                    if (it > 0) 1 else it
                }
            }

            else -> 0
        }
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
                "VT_SCHEDULE_DATE",
                "VT_LIVE_CHANNEL_PRODUCT",
                "VT_UPCOMING_VERTICAL"
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