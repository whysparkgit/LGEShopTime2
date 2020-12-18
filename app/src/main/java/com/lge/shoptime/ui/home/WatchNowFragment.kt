package com.lge.shoptime.ui.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lge.shoptime.R

class WatchNowFragment : Fragment() {

    companion object {
        fun newInstance() = WatchNowFragment()
    }

    private lateinit var viewModel: WatchNowViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_watchnow, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WatchNowViewModel::class.java)
        // TODO: Use the ViewModel
    }

}