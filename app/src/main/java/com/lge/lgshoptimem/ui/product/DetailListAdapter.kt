package com.lge.lgshoptimem.ui.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.BR
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.*
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.BaseListComponent
import com.lge.lgshoptimem.ui.component.ComponentItemListener
import java.util.*

class DetailListAdapter(mActivity: AppCompatActivity):
        RecyclerView.Adapter<DetailListAdapter.ItemViewHolder<*>>()
{
    private val mViewModel = mActivity.run {
        val viewModel: DetailViewModel by viewModels()
        viewModel
    }

    private val marrViewTypes = arrayOf(
        AppConst.VIEWTYPE.VT_PRODUCT_DETAIL,
        AppConst.VIEWTYPE.VT_PRODUCT_REVIEW,
        AppConst.VIEWTYPE.VT_YOU_MAY_LIKE
    )

    private val mListener: ComponentItemListener = mActivity as ComponentItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<*> {
//        Trace.debug("++ onCreateViewHolder() viewType = $viewType")
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            AppConst.VIEWTYPE.VT_PRODUCT_DETAIL -> {
                val binding: ViewProductDetailBinding = DataBindingUtil.inflate(inflater, R.layout.view_product_detail, parent, false)
                ItemViewHolder<ViewProductDetailBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_PRODUCT_REVIEW -> {
                val binding: ViewProductReviewsBinding = DataBindingUtil.inflate(inflater, R.layout.view_product_reviews, parent, false)
                ItemViewHolder<ViewProductReviewsBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE -> {
                val binding: ViewYouMayLikeBinding = DataBindingUtil.inflate(inflater, R.layout.view_you_may_like, parent, false)
                ItemViewHolder<ViewYouMayLikeBinding>(binding.root)
            }

            else -> {
                // TODO not exit view
                val binding: ViewProductListBinding = DataBindingUtil.inflate(inflater, R.layout.view_product_list, parent, false)
                ItemViewHolder<ViewProductListBinding>(binding.root)
            }
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder<*>, position: Int) {
        val binding: ViewDataBinding? = holder.getBinding()
        Trace.debug("++ onBindViewHolder() position = $position binding = $binding")

        if (mViewModel.mldDetail.value == null) return

        binding?.setVariable(BR.position, position)
        binding?.setVariable(BR.listener, mListener)

        val baseCompList = binding?.root?.findViewById<BaseListComponent>(R.id.comp_list)

        when (getItemViewType(position)) {
            AppConst.VIEWTYPE.VT_PRODUCT_DETAIL -> {
                binding?.setVariable(BR.viewdata, mViewModel.mldDetail.value!!.product[position])
//                baseCompList?.setHeadData(mViewModel.mldWatchNow.value!!.productInfos)
                baseCompList?.setItemList(mViewModel.mldDetail.value!!.product[position].imgUrls)
            }

            AppConst.VIEWTYPE.VT_PRODUCT_REVIEW -> {
//                baseCompList?.setHeadData(mViewModel.mldWatchNow.value!!.upcomingItems)
//                baseCompList?.setItemList(mViewModel.mldDetail.value!!.upcomingItems)
            }

            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE -> {
//                baseCompList?.setHeadData(mViewModel.mldWatchNow.value!!.youmaylike)
                baseCompList?.setItemList(mViewModel.mldDetail.value!!.youmaylike)
            }

            else -> { }
        }

        holder.setItemListener()
    }

    override fun getItemCount(): Int {
        var nCount = 0

        marrViewTypes.forEach {
            nVal -> nCount += getItemCount(nVal)
        }

//        Trace.debug("++ getItemCount() nCount = $nCount")
        return nCount
    }

    fun getItemCount(viewType: Int): Int {
//        Trace.debug("++ getItemCount() viewType = $viewType")

        if (mViewModel.mldDetail.value == null) return 0

        return when (viewType) {
            AppConst.VIEWTYPE.VT_PRODUCT_DETAIL -> {             // product
                if (mViewModel.mldDetail.value!!.product.size > 0) 1 else 0
            }

            AppConst.VIEWTYPE.VT_PRODUCT_REVIEW -> 0    //{       // upcoming(1)
//                if (mViewModel.mldDetail.value!!.reviews.size > 0) 1 else 0
//            }

            AppConst.VIEWTYPE.VT_YOU_MAY_LIKE -> 0      //{              // youMayLike(10)
//                if (mViewModel.mldDetail.value!!.youmaylike.size > 0) 1 else 0
//            }

            else -> 0
        }
    }

    override fun getItemViewType(position: Int): Int {
        var nIndex: Int = 0

        marrViewTypes.forEach {
            nVal -> if (position in nIndex until nIndex + getItemCount(nVal)) {
//                Trace.debug("++ getItemViewType() position = $position ViewType = $nVal")
                return nVal
            }

            nIndex += getItemCount(nVal)
        }

        return marrViewTypes.last()
    }

    fun getPositionOf(viewType: Int, position: Int): Int {
        var nCount: Int = 0

        marrViewTypes.forEach {
            nVal -> if (nVal == viewType) {
                Trace.debug("++ getPositionOf() result = ${position - nCount}")
                return (position - nCount).let {
                    if (it < 0) 0
                    else it
                }
            } else {
                nCount += getItemCount(nVal)
            }
        }

        return 0
    }

    fun getViewTypeString(position: Int): String {
        val nType: Int = getItemViewType(position);

        val strViewTypes = arrayOf(
                "VT_PRODUCT_DETAIL",
                "VT_PRODUCT_REVIEW",
                "VT_YOU_MAY_LIKE"
        )

        marrViewTypes.forEachIndexed { index, nVal -> if (nVal == nType) return strViewTypes[index] }

        return ""
    }

    inner class ItemViewHolder<B>(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        fun getBinding() = DataBindingUtil.getBinding<ViewDataBinding>(itemView)

        fun setItemListener() {
            Trace.debug("++ bind()")
            val view: View? = itemView.findViewById<ConstraintLayout>(R.id.comp_list)

            view?.apply {
                if (this is BaseListComponent) {
                    Trace.debug(">> view is BaseListComponent")
                    addItemListener(mListener)
                }
            }
        }
    }
}