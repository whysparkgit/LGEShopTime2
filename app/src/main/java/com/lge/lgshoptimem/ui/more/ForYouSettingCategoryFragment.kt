package com.lge.lgshoptimem.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.FragmentForyouSettingCategoryBinding
import com.lge.lgshoptimem.model.dto.CategoryList
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.ComponentItemListener

class ForYouSettingCategoryFragment : Fragment(), ComponentItemListener {
    private lateinit var mBinding: FragmentForyouSettingCategoryBinding
    private val mViewModel: ForYouSettingViewModel by activityViewModels()
    private var mLaunchFrom: Int = AppConst.VALUE.INITIAL

    companion object {
        fun newInstance() = ForYouSettingCategoryFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        mBinding = FragmentForyouSettingCategoryBinding.inflate(inflater, container, false)
        mBinding.listener = this

        arguments?.let {
            mLaunchFrom = it.getInt(AppConst.KEY.LAUNCH_FROM, AppConst.VALUE.INITIAL)
        }

        mBinding.launchFrom = mLaunchFrom
        mViewModel.mldCategoryList.observe(viewLifecycleOwner, this::onDataListChanged)

        mViewModel.requestCategory()

        return mBinding.root
    }

    private fun onDataListChanged(itemList: CategoryList.Response.Data) {
        Trace.debug("++ onDataListChanged()")

        if (itemList.categoryInfos.isNullOrEmpty()) return

        mBinding.compList.setItemList(itemList.categoryInfos)
        mBinding.compList.addItemListener(this)
    }

    fun onClick(v: View) {
        Trace.debug("++ onClick() v = ${v.id}")

        when (v.id) {
            R.id.fys_btn_save -> mViewModel.saveFavoriteCategory()

            R.id.fys_btn_cancel -> activity?.onBackPressed()
        }
    }

    override fun onClick(v: View, pos: Int) {
        Trace.debug("++ onClick() v = ${v.id} pos = $pos")
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
        Trace.debug("++ onItemClick() parent = ${parent.id} parentPos = $parentPos item = ${item.id} pos = $pos")
    }
}