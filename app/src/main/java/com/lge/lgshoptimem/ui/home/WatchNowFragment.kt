package com.lge.lgshoptimem.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.databinding.FragmentWatchnowBinding
import com.lge.lgshoptimem.databinding.ViewLiveChannelsBinding
import com.lge.lgshoptimem.databinding.ViewPopularShowsBinding
import com.lge.lgshoptimem.model.dto.Curation
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.ComponentItemListener
import com.lge.lgshoptimem.ui.component.VideoViewComponent

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

        mViewModel.mldDataList.observe(viewLifecycleOwner, this::onDataListChanged)
        mViewModel.requestData()
    }

    private fun onDataListChanged(itemList: ArrayList<Curation>) {
        Trace.debug("++ onDataListChanged()")
        mAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View, pos: Int) {
        Trace.debug("++ onClick() v = ${v.id} pos = $pos")

        when (mAdapter.getItemViewType(pos)) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> Trace.debug(">> viewType = VT_LIVE_CHANNEL_PRODUCT")
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL -> Trace.debug(">> viewType = VT_NEXT_UPCOMING_HORIZONTAL")
            AppConst.VIEWTYPE.VT_TODAY_DEAL -> Trace.debug(">> viewType = VT_TODAY_DEAL")
            AppConst.VIEWTYPE.VT_POPULAR_SHOWS -> Trace.debug(">> viewType = VT_POPULAR_SHOWS")
            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE -> Trace.debug(">> viewType = VT_YOU_MAY_LIKE")
            else -> Trace.debug(">> viewType = else")
        }

        val intent = Intent(context, VideoPlayerActivity::class.java)
        intent.putExtra("param", "https://wowzaprod134-i.akamaihd.net/hls/live/577814/ccddaf02/playlist.m3u8")
        startActivity(intent)
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
        Trace.debug("++ onItemClick() parent = ${parent.id} parentPos = $parentPos item = ${item.id} pos = $pos")

        when (mAdapter.getItemViewType(parentPos)) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNEL_ICONS -> {
                item.isSelected = !item.isSelected
            }

            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> Trace.debug(">> viewType = VT_LIVE_CHANNEL_PRODUCT")
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL -> Trace.debug(">> viewType = VT_NEXT_UPCOMING_HORIZONTAL")
            AppConst.VIEWTYPE.VT_TODAY_DEAL -> Trace.debug(">> viewType = VT_TODAY_DEAL")
            AppConst.VIEWTYPE.VT_POPULAR_SHOWS -> Trace.debug(">> viewType = VT_POPULAR_SHOWS")
            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE -> Trace.debug(">> viewType = VT_YOU_MAY_LIKE")
            else -> Trace.debug(">> viewType = else")
        }
    }

    override fun onDestroyView() {
        Trace.debug("++ onDestroyView()")
        super.onDestroyView()
    }
}