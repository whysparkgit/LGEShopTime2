package com.lge.shoptime.ui.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lge.shoptime.R

class ForYouFragment : Fragment() {

    companion object {
        fun newInstance() = ForYouFragment()
    }

    private lateinit var viewModel: ForYouViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_foryou, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ForYouViewModel::class.java)
        // TODO: Use the ViewModel
    }

}