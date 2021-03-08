package com.lge.lgshoptimem.ui.more

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.CompItemKeywordSetBinding
import com.lge.lgshoptimem.model.dto.KeywordList

class ForYouSettingKeywordAdapter(val mFragment: ForYouSettingKeywordFragment): RecyclerView.Adapter<ForYouSettingKeywordAdapter.ItemViewHolder>()
{
    private val mViewModel = mFragment.run {
        val viewModel: ForYouSettingViewModel by activityViewModels()
        viewModel
    }

    private var marrIndex: ArrayList<Int> = ArrayList()
    private val MAX_LINE_LENGTH: Int = 25

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: CompItemKeywordSetBinding = DataBindingUtil.inflate(inflater, R.layout.comp_item_keyword_set, parent, false)
        return ItemViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        Trace.debug("++ onBindViewHolder() position = $position")

        if (mViewModel.mldKeywordList.value?.keywords.isNullOrEmpty()) return

        val binding = holder.getBinding()
        binding.listener = mFragment
//        val nPos = position * 3
        var nPos = 0

        for (i in 0 until position) {
            nPos += marrIndex[i]
        }

        binding.position = nPos
        val keywordSet: ArrayList<KeywordList.Keyword> = arrayListOf()

//        val nCount = mViewModel.mldCategoryList.value!!.tagInfos.size
//
//        for (i in 0..2) {
//            if (nPos + i < nCount) {
//                Trace.debug(">> onBindViewHolder() position = $position nCount = $nCount nPos = ${nPos + i}")
//                tagSet.add(mViewModel.mldCategoryList.value!!.tagInfos[nPos + i])
//            }
//        }

        for (i in 0 until marrIndex[position]) {
            Trace.debug(">> marrIndex[position] = ${marrIndex[position]} nPos = ${nPos + i}")
            keywordSet.add(mViewModel.mldKeywordList.value!!.keywords[nPos + i])
        }

        binding.viewdata = keywordSet
    }

    override fun getItemCount(): Int {
//        val nCount: Double = mViewModel.mldCategoryList.value!!.tagInfos.size.toDouble()
//        return ceil(nCount / 3.0).toInt()
        return marrIndex.size
    }

    fun measure() {
        if (mViewModel.mldKeywordList.value?.keywords.isNullOrEmpty()) return

        var nLineLength = 0
        var nLineCount = 0

        for (i in 0 until mViewModel.mldKeywordList.value!!.keywords.size) {
            nLineLength += mViewModel.mldKeywordList.value!!.keywords[i].keywd.length
            nLineCount++

            if (nLineLength > MAX_LINE_LENGTH) {
                marrIndex.add(nLineCount - 1)
                nLineLength = mViewModel.mldKeywordList.value!!.keywords[i].keywd.length
                nLineCount = 1
                continue
            }

            if (nLineCount == 3) {
                marrIndex.add(nLineCount)
                nLineLength = 0
                nLineCount = 0
            }
        }

        if (nLineCount > 0) marrIndex.add(nLineCount)

        Trace.debug("++ measure() marrIndex = $marrIndex")
    }

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun getBinding() = DataBindingUtil.getBinding<CompItemKeywordSetBinding>(itemView) as CompItemKeywordSetBinding
    }
}