package com.lge.lgshoptimem.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.FragmentForyouBinding
import com.lge.lgshoptimem.model.dto.Curation
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.ComponentItemListener
import com.lge.lgshoptimem.ui.component.HeaderGridComponent
import com.lge.lgshoptimem.ui.component.HeaderListComponent

class ForYouFragment : Fragment(), ComponentItemListener {
    private lateinit var mBinding: FragmentForyouBinding
    private lateinit var mAdapter: ForYouListAdapter
    private val mViewModel: ForYouViewModel by viewModels()

    companion object {
        fun newInstance() = ForYouFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        mBinding = FragmentForyouBinding.inflate(inflater, container, false)
        mBinding.listener = this

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mAdapter = ForYouListAdapter(this)

        mBinding.fyRvMainList.apply {
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
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL -> Trace.debug(">> viewType = VT_NEXT_UPCOMING_HORIZONTAL")
            AppConst.VIEWTYPE.VT_CATEGORY_REMINDER -> Trace.debug(">> viewType = VT_CATEGORY_REMINDER")
            AppConst.VIEWTYPE.VT_KEYWORD_REMINDER -> Trace.debug(">> viewType = VT_KEYWORD_REMINDER")
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_VERTICAL -> Trace.debug(">> viewType = VT_NEXT_UPCOMING_VERTICAL")
            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> Trace.debug(">> viewType = VT_LIVE_CHANNEL_PRODUCT")
            AppConst.VIEWTYPE.VT_MY_FAVORITES -> Trace.debug(">> viewType = VT_MY_FAVORITES")
            AppConst.VIEWTYPE.VT_RECENTLY_VIEWED -> Trace.debug(">> viewType = VT_RECENTLY_VIEWED")
            AppConst.VIEWTYPE.VT_COUPON -> Trace.debug(">> viewType = VT_COUPON")
            else -> Trace.debug(">> viewType = else")
        }

        when (v) {
            is HeaderListComponent -> Trace.debug(">> v = HeaderListComponent")
            is HeaderGridComponent -> Trace.debug(">> v = HeaderGridComponent")
            is ConstraintLayout -> Trace.debug(">> v = ConstraintLayout")
            else -> Trace.debug(">> v = else")
        }

//        val intent = Intent(context, VideoPlayerActivity::class.java)
//        intent.putExtra("param", "https://wowzaprod134-i.akamaihd.net/hls/live/577814/ccddaf02/playlist.m3u8")
//        startActivity(intent)
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
        Trace.debug("++ onItemClick() parent = ${parent.id} parentPos = $parentPos item = ${item.id} pos = $pos")

        when (mAdapter.getItemViewType(parentPos)) {
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL -> Trace.debug(">> viewType = VT_NEXT_UPCOMING_HORIZONTAL")
            AppConst.VIEWTYPE.VT_CATEGORY_REMINDER -> Trace.debug(">> viewType = VT_CATEGORY_REMINDER")
            AppConst.VIEWTYPE.VT_KEYWORD_REMINDER -> Trace.debug(">> viewType = VT_KEYWORD_REMINDER")
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_VERTICAL -> Trace.debug(">> viewType = VT_NEXT_UPCOMING_VERTICAL")
            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> Trace.debug(">> viewType = VT_LIVE_CHANNEL_PRODUCT")
            AppConst.VIEWTYPE.VT_MY_FAVORITES -> Trace.debug(">> viewType = VT_MY_FAVORITES")
            AppConst.VIEWTYPE.VT_RECENTLY_VIEWED -> Trace.debug(">> viewType = VT_RECENTLY_VIEWED")
            AppConst.VIEWTYPE.VT_COUPON -> Trace.debug(">> viewType = VT_COUPON")
            else -> Trace.debug(">> viewType = else")
        }

        when (parent) {
            is HeaderListComponent -> Trace.debug(">> parent = HeaderListComponent")
            is HeaderGridComponent -> Trace.debug(">> parent = HeaderGridComponent")
            is ConstraintLayout -> Trace.debug(">> parent = ConstraintLayout")
            else -> Trace.debug(">> parent = else")
        }

        when (parent.id) {
            R.id.comp_cl_main -> Trace.debug(">> parent = R.id.comp_cl_main")
            else -> Trace.debug(">> parent = else")
        }

        when (item) {
            is ConstraintLayout -> Trace.debug(">> item = ConstraintLayout")
            else -> Trace.debug(">> item = else")
        }

        when (item.id) {
            R.id.comp_cl_item -> Trace.debug(">> item = R.id.comp_cl_item")
            else -> Trace.debug(">> item = else")
        }
    }
}