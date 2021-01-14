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

        mBinding.wnRvMainList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }
    }

    override fun onClick(v: View, pos: Int) {
        Trace.debug("++ onClick() v = ${v.id} pos = $pos")

        val intent = Intent(context, VideoPlayerActivity::class.java)
        intent.putExtra("param", "https://wowzaprod134-i.akamaihd.net/hls/live/577814/ccddaf02/playlist.m3u8")
        startActivity(intent)
    }

}