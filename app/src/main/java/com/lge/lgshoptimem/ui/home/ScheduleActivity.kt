package com.lge.lgshoptimem.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.lge.core.sys.Trace
import com.lge.core.sys.Util
import com.lge.core.sys.Util.Companion.toDateFormat
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityScheduleBinding
import com.lge.lgshoptimem.model.dto.Schedule
import com.lge.lgshoptimem.model.dto.ScheduleDate
import com.lge.lgshoptimem.model.dto.UpcomingAlarm
import com.lge.lgshoptimem.ui.common.ActionBar
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.BaseListComponent
import com.lge.lgshoptimem.ui.component.ComponentItemListener


class ScheduleActivity : AppCompatActivity(), ActionBar.onActionBarListener , ComponentItemListener{

    private lateinit var mBinding: ActivityScheduleBinding
    private lateinit var mAdapter: ScheduleListAdapter
    private val mViewModel: ScheduleViewModel by viewModels()

    val mScheduleDates: ArrayList<ScheduleDate> = ArrayList()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_schedule)
        mBinding.listener = this

        mAdapter = ScheduleListAdapter(this)

        mBinding.ascRvMainList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

        mViewModel.mldSchedule.observe(this, this::onDataListChanged)
        mViewModel.requestData("1", Util.GetNowDateFormat("yyyy-MM-dd HH:mm:ss"))

        initActionBar()
    }

    private fun initActionBar() {
        val actionBar = ActionBar(this)
        actionBar.title = getString(R.string.schedule)
        actionBar.setButton(ActionBar.ACTION_BACK, ActionBar.ACTION_NONE)
        mBinding.actionbar = actionBar
    }

    private fun onDataListChanged(itemList: Schedule.Response.Data) {
        Trace.debug("++ onDataListChanged()")
        mAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View, pos: Int) {
        Trace.debug("++ onClick() v = ${v.id} pos = $pos")

        when (mAdapter.getItemViewType(pos)) {
            AppConst.VIEWTYPE.VT_SCHEDULE_DATE -> {
                Trace.debug(">> viewType = VT_LIVE_CHANNEL_PRODUCT")
            }

            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {}

            AppConst.VIEWTYPE.VT_UPCOMING_VERTICAL -> Trace.debug(">> viewType = VT_NEXT_UPCOMING_HORIZONTAL")
            else -> Trace.debug(">> viewType = else")
        }
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
        Trace.debug("++ onItemClick() parent = ${parent.id} parentPos = $parentPos item = ${item.id} pos = $pos")

        when (mAdapter.getItemViewType(parentPos)) {
            AppConst.VIEWTYPE.VT_SCHEDULE_DATE -> {
                if (mAdapter.mScheduleShowIndex == pos) return

//                mAdapter.mScheduleDates.forEach { it.selected = false }
//                mAdapter.mScheduleDates[pos].selected = true
//                mAdapter.mShowInfoIndex = pos
//                mBinding.ascRvMainList.getChildAt(parentPos).findViewById<BaseListComponent>(R.id.comp_list).refresh()

                for (i in 0 until mAdapter.mScheduleDates.size) {
                    if (i == pos) {
                        mAdapter.mScheduleDates[i].selected = true
                        mBinding.ascRvMainList.getChildAt(parentPos).findViewById<BaseListComponent>(R.id.comp_list).refreshItem(i)
                    } else if (mAdapter.mScheduleDates[i].selected) {
                        mAdapter.mScheduleDates[i].selected = false
                        mBinding.ascRvMainList.getChildAt(parentPos).findViewById<BaseListComponent>(R.id.comp_list).refreshItem(i)
                    }
                }

                mAdapter.mScheduleShowIndex = pos
            }

            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {
//                mAdapter.mChannelIcons.forEach { it.selected = false }
//                mAdapter.mChannelIcons[pos].selected = true
//                mAdapter.mChannelShowIndex = pos
//                mBinding.ascRvMainList.getChildAt(parentPos).findViewById<BaseListComponent>(R.id.comp_list).refresh()

                for (i in 0 until mAdapter.mChannelIcons.size) {
                    if (i == pos) {
                        mAdapter.mChannelIcons[i].selected = true
                        mBinding.ascRvMainList.getChildAt(parentPos).findViewById<BaseListComponent>(R.id.comp_list).refreshItem(i)
                    } else if (mAdapter.mChannelIcons[i].selected) {
                        mAdapter.mChannelIcons[i].selected = false
                        mBinding.ascRvMainList.getChildAt(parentPos).findViewById<BaseListComponent>(R.id.comp_list).refreshItem(i)
                    }
                }

                mAdapter.mChannelShowIndex = pos

                val partnerId = mViewModel.mldSchedule.value!!.partnerInfos[pos].patnrId
                val lDate = Util.getSpecificTime(0, 0, mAdapter.mScheduleShowIndex, 0, 0, 0)
                Trace.debug(">> startDate = ${lDate.toDateFormat("yyyy-MM-dd HH:mm:ss")}")
                mViewModel.requestData(partnerId, lDate.toDateFormat("yyyy-MM-dd HH:mm:ss"))
            }

            AppConst.VIEWTYPE.VT_UPCOMING_VERTICAL -> {
                when (item.id) {
                    R.id.comp_tb_reminder -> {
//                        Trace.debug(">> isChecked =${(item as ToggleButton).isChecked}")
//                        val request = UpcomingAlarm.Request(
//                                mViewModel.mldSchedule.value!!.upcomingItems[pos].patnrId,
//                                mViewModel.mldSchedule.value!!.upcomingItems[pos].showId,
//                                mViewModel.mldSchedule.value!!.upcomingItems[pos].strtDt,
//                                mViewModel.mldSchedule.value!!.upcomingItems[pos].endDt,
//                                if ((item as ToggleButton).isChecked) "Y" else "N"
//                        )
//
//                        mViewModel.requestUpcomingAlarm(request)
                    }
                }
            }

            else -> Trace.debug(">> viewType = else")
        }
    }

    //Top ActionBar clickListener
    override fun onLeft() {
        onBackPressed()
    }

    override fun onRight() {
        //Do Nothing
    }
}