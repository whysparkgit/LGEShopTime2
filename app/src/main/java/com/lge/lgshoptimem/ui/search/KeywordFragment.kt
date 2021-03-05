package com.lge.lgshoptimem.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lge.lgshoptimem.databinding.FragmentSearchKeywordBinding

class KeywordFragment : Fragment()
{
    private lateinit var mBinding: FragmentSearchKeywordBinding

    companion object {
        fun newInstance() = KeywordFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        mBinding = FragmentSearchKeywordBinding.inflate(inflater, container, false)
//        mBinding.listener = this

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }


}