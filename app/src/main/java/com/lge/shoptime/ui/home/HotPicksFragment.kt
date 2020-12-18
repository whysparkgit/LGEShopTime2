package com.lge.shoptime.ui.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lge.shoptime.R

class HotPicksFragment : Fragment() {

    companion object {
        fun newInstance() = HotPicksFragment()
    }

    private lateinit var viewModel: HotPicksViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hotpicks, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HotPicksViewModel::class.java)
        // TODO: Use the ViewModel
    }

}