package com.lge.lgshoptimem.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.BR
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.FragmentHotpicksBinding
import com.lge.lgshoptimem.databinding.FragmentSearchResultBinding
import com.lge.lgshoptimem.model.dto.HotPicks
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.ComponentItemListener
import com.lge.lgshoptimem.ui.product.DetailActivity

class ResultFragment : Fragment()
{
    private lateinit var mBinding: FragmentSearchResultBinding

    companion object {
        fun newInstance() = ResultFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        mBinding = FragmentSearchResultBinding.inflate(inflater, container, false)
//        mBinding.listener = this

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}