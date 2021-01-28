package com.lge.lgshoptimem.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.databinding.FragmentHotpicksBinding
import com.lge.lgshoptimem.model.dto.Curation
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.ComponentItemListener

class HotPicksFragment : Fragment(), ComponentItemListener
{
    private lateinit var mBinding: FragmentHotpicksBinding
    private lateinit var mAdapter: HotPicksListAdapter
    private val mViewModel: HotPicksViewModel by viewModels()

    companion object {
        fun newInstance() = HotPicksFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        mBinding = FragmentHotpicksBinding.inflate(inflater, container, false)
        mBinding.listener = this

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mAdapter = HotPicksListAdapter(this)

        mBinding.hpRvMainList.apply {
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
            AppConst.VIEWTYPE.VT_HOT_PICKS -> Trace.debug(">> viewType = VT_HOT_PICKS")
            else -> Trace.debug(">> viewType = else")
        }

        val intent = Intent(context, VideoPlayerActivity::class.java)
        intent.putExtra("param", "https://wowzaprod134-i.akamaihd.net/hls/live/577814/ccddaf02/playlist.m3u8")
        startActivity(intent)
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
        Trace.debug("++ onItemClick() parent = ${parent.id} parentPos = $parentPos item = ${item.id} pos = $pos")

        when (mAdapter.getItemViewType(parentPos)) {
            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> Trace.debug(">> viewType = VT_LIVE_CHANNEL_PRODUCT")
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL -> Trace.debug(">> viewType = VT_NEXT_UPCOMING_HORIZONTAL")
            AppConst.VIEWTYPE.VT_TODAY_DEAL -> Trace.debug(">> viewType = VT_TODAY_DEAL")
            AppConst.VIEWTYPE.VT_HOT_PICKS -> Trace.debug(">> viewType = VT_HOT_PICKS")
            else -> Trace.debug(">> viewType = else")
        }
    }
}