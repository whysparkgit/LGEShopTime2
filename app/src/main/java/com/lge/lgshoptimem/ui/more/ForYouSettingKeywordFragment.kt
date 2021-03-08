package com.lge.lgshoptimem.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.FragmentForyouSettingKeywordBinding
import com.lge.lgshoptimem.model.dto.CategoryList
import com.lge.lgshoptimem.model.dto.KeywordList
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.ComponentItemListener

class ForYouSettingKeywordFragment : Fragment(), ComponentItemListener {
    private lateinit var mBinding: FragmentForyouSettingKeywordBinding
    private lateinit var mAdapter: ForYouSettingKeywordAdapter
    private val mViewModel: ForYouSettingViewModel by activityViewModels()
    private var mLaunchFrom: Int = AppConst.VALUE.INITIAL

    companion object {
        fun newInstance() = ForYouSettingKeywordFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        mBinding = FragmentForyouSettingKeywordBinding.inflate(inflater, container, false)
        mBinding.listener = this

        arguments?.let {
            mLaunchFrom = it.getInt(AppConst.KEY.LAUNCH_FROM, AppConst.VALUE.INITIAL)
        }

        mBinding.launchFrom = mLaunchFrom
        mAdapter = ForYouSettingKeywordAdapter(this)

        mBinding.fysRvMainList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

        mViewModel.mldKeywordList.observe(viewLifecycleOwner, this::onDataListChanged)

        mViewModel.requestKeyword()

        return mBinding.root
    }

    private fun onDataListChanged(itemList: KeywordList.Response.Data) {
        if (itemList.keywords.isNullOrEmpty()) return

        Trace.debug("++ onDataListChanged() keywords = ${itemList.keywords.size}")

        mAdapter.measure()
        mAdapter.notifyDataSetChanged()

//        val tags: ArrayList<ArrayList<CategoryList.Tag>> = arrayListOf()
//
//        for (i in 0..itemList.tagInfos.size step 3) {
//            Trace.debug(">> itemList.tagInfos i = $i")
//            val tagSet: ArrayList<CategoryList.Tag> = arrayListOf()
//
//            if (i < itemList.tagInfos.size) tagSet.add(itemList.tagInfos[i])
//            if (i + 1 < itemList.tagInfos.size) tagSet.add(itemList.tagInfos[i + 1])
//            if (i + 2 < itemList.tagInfos.size) tagSet.add(itemList.tagInfos[i + 2])
//
//            tags.add(tagSet)
//        }
//
//        mBinding.compList.setItemList(tags)
//        mBinding.compList.addItemListener(this)

//        setTableData(itemList.tagInfos)
    }

    fun onClick(v: View) {
        Trace.debug("++ onClick() v = ${v.id}")

        when (v.id) {
            R.id.fys_btn_done -> {
                mViewModel.mldKeywordList.value?.keywords?.forEach {
                    Trace.debug(">> name = ${it.keywd} like = ${it.bFavorite}")
                }

                mViewModel.saveFavoriteKeyword()
            }
        }
    }

    override fun onClick(v: View, pos: Int) {
        Trace.debug("++ onClick() v = ${v.id} pos = $pos")
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
        Trace.debug("++ onItemClick() parent = ${parent.id} parentPos = $parentPos item = ${item.id} pos = $pos")
    }

//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun setTableData(itemList: ArrayList<CategoryList.Tag>) {
//        if (itemList.isNullOrEmpty()) return
//
//        val gridLayout: GridLayout = mBinding.fysGlTable
//        gridLayout.removeAllViews()
//
//        val nColumnCount = 3
//        Trace.debug(">> nColumnCount = $nColumnCount")
//
//        gridLayout.columnCount = nColumnCount
//        gridLayout.rowCount = itemList.size + 1
//
//        for (row in 0 until itemList.size) {
//            for (col in 0 until nColumnCount) {
//                val titleText = TextView(context)
//                val layoutParams = GridLayout.LayoutParams()
//                layoutParams.width = 0
//                layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT
//                layoutParams.columnSpec = GridLayout.spec(col, 1, GridLayout.FILL, 1f)
//                layoutParams.rowSpec = GridLayout.spec(row, 1, GridLayout.FILL)
//
//                titleText.setText(itemList[row].catNm)
//
//                titleText.layoutParams = layoutParams
//                titleText.textAlignment = View.TEXT_ALIGNMENT_CENTER
//                titleText.gravity = Gravity.LEFT
//                titleText.setPadding(0, 12, 0, 12)
//                titleText.setTextColor(ApplicationProxy.getContext().getColor(R.color.black_141414))
//                titleText.setBackgroundResource(R.drawable.bg_rect_border_gray_r2)
//
//                val index = nColumnCount * row + col
//                gridLayout.addView(titleText, index)
//                Trace.debug(">> coordination[$index] = [$row, $col]")
//            }
//        }
//    }
}