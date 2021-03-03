package com.lge.lgshoptimem.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityScheduleBinding
import com.lge.lgshoptimem.model.dto.Schedule
import com.lge.lgshoptimem.ui.common.ActionBar
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.BaseListComponent
import com.lge.lgshoptimem.ui.component.ComponentItemListener


class ScheduleActivity : AppCompatActivity(), ActionBar.onActionBarListener , ComponentItemListener{

    private lateinit var mBinding: ActivityScheduleBinding
    private lateinit var mAdapter: ScheduleListAdapter

    val mSchedules: ArrayList<Schedule> = ArrayList()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_schedule)
        mBinding.activity = this

        //actionBar setting
        val actionBar = ActionBar(this)
        actionBar.title = getString(R.string.schedule)
        actionBar.setButton(ActionBar.ACTION_BACK, ActionBar.ACTION_NONE)
        mBinding.actionbar = actionBar
        mBinding.listener = this

        mAdapter = ScheduleListAdapter(this)

//        mBinding.ascRvMainList.itemAnimator = null
        mBinding.ascRvMainList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

        //viewmodel
    }

//    private fun onDataListChanged(itemList: WatchNow.Response.Data) {
//        Trace.debug("++ onDataListChanged()")
//        mAdapter.notifyDataSetChanged()
//    }

    override fun onClick(v: View, pos: Int) {
        Trace.debug("++ onClick() v = ${v.id} pos = $pos")

        when (mAdapter.getItemViewType(pos)) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> {
                Trace.debug(">> viewType = VT_LIVE_CHANNEL_PRODUCT")

//                when (v.id) {
//                    R.id.comp_tv_more -> {
//                        val itemBinding: ViewDataBinding? = (mBinding.ascRvMainList.findViewHolderForAdapterPosition(pos) as WatchNowListAdapter.ItemViewHolder<*>).getBinding()
//                        var nCount = itemBinding?.root?.findViewById<BaseListComponent>(R.id.comp_list)?.mAdapter?.itemCount

//                        Trace.debug(">> mAdapter.itemCount = $nCount")
//                        Trace.debug(">> productInfos.size = ${mViewModel.mldWatchNow.value!!.productInfos.size}")
//
//                        if (mViewModel.mldWatchNow.value!!.productInfos.size > nCount!! + 10) {
//                            itemBinding?.root?.findViewById<BaseListComponent>(R.id.comp_list)?.setItemCountLimit(nCount + 10)
//                        } else if (mViewModel.mldWatchNow.value!!.productInfos.size > nCount) {
//                            nCount = mViewModel.mldWatchNow.value!!.productInfos.size
//                            itemBinding?.root?.findViewById<BaseListComponent>(R.id.comp_list)?.setItemCountLimit(nCount)
//                        }
//
//                        itemBinding?.root?.findViewById<BaseListComponent>(R.id.comp_list)?.refresh()
//                        mAdapter.notifyDataSetChanged()
//                    }
//                }
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

        when (mAdapter.getItemViewType(parentPos)) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {
                if (mAdapter.mShowInfoIndex == pos) return

                mAdapter.mSchedules.forEach { it.selected = false }
                mAdapter.mSchedules[pos].selected = true
                mAdapter.mShowInfoIndex = pos
                mBinding.ascRvMainList.getChildAt(parentPos).findViewById<BaseListComponent>(R.id.comp_list).refresh()
//                mAdapter.notifyDataSetChanged()

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