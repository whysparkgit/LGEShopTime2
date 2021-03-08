package com.lge.lgshoptimem.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.BR
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.*
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.BaseListComponent
import com.lge.lgshoptimem.ui.component.ComponentItemListener

class ForYouListAdapter(mFragment: Fragment):
        RecyclerView.Adapter<ForYouListAdapter.ItemViewHolder<*>>()
{
    private val mViewModel = mFragment.run {
        val viewModel: ForYouViewModel by viewModels()
        viewModel
    }

    val marrViewTypes = arrayOf(
        AppConst.VIEWTYPE.VT_FAVORITE_CATEGORY,
        AppConst.VIEWTYPE.VT_FAVORITE_KEYWORD,
        AppConst.VIEWTYPE.VT_UPCOMING_HORIZONTAL,
        AppConst.VIEWTYPE.VT_MY_FAVORITES,
        AppConst.VIEWTYPE.VT_RECENTLY_VIEWED,
        AppConst.VIEWTYPE.VT_COUPON
    )

    private val mListener: ComponentItemListener = mFragment as ComponentItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForYouListAdapter.ItemViewHolder<*> {
//        Trace.debug("++ onCreateViewHolder() viewType = $viewType")
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            AppConst.VIEWTYPE.VT_FAVORITE_CATEGORY -> {
                val binding: ViewFavoriteCategoryBinding = DataBindingUtil.inflate(inflater, R.layout.view_favorite_category, parent, false)
                ItemViewHolder<ViewFavoriteCategoryBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_FAVORITE_KEYWORD -> {
                val binding: ViewFavoriteKeywordBinding = DataBindingUtil.inflate(inflater, R.layout.view_favorite_keyword, parent, false)
                ItemViewHolder<ViewFavoriteKeywordBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_UPCOMING_HORIZONTAL -> {
                val binding: ViewUpcomingHorizontalBinding = DataBindingUtil.inflate(inflater, R.layout.view_upcoming_horizontal, parent, false)
                ItemViewHolder<ViewUpcomingHorizontalBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_MY_FAVORITES -> {
                val binding: ViewMyFavoritesBinding = DataBindingUtil.inflate(inflater, R.layout.view_my_favorites, parent, false)
                ItemViewHolder<ViewMyFavoritesBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_RECENTLY_VIEWED -> {
                val binding: ViewRecentlyViewedBinding = DataBindingUtil.inflate(inflater, R.layout.view_recently_viewed, parent, false)
                ItemViewHolder<ViewRecentlyViewedBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_COUPON -> {
                val binding: ViewCouponHorizontalBinding = DataBindingUtil.inflate(inflater, R.layout.view_coupon_horizontal, parent, false)
                ItemViewHolder<ViewCouponHorizontalBinding>(binding.root)
            }

            else -> {
                // TODO not exit view
                val binding: ViewProductListBinding = DataBindingUtil.inflate(inflater, R.layout.view_product_list, parent, false)
                ItemViewHolder<ViewProductListBinding>(binding.root)
            }
        }
    }

    override fun onBindViewHolder(holder: ForYouListAdapter.ItemViewHolder<*>, position: Int) {
        val binding: ViewDataBinding? = holder.getBinding()
        Trace.debug("++ onBindViewHolder() position = $position binding = $binding")

        if (mViewModel.mldForYou.value == null) return

        binding?.setVariable(BR.position, position)
        binding?.setVariable(BR.listener, mListener)

        val baseCompList = binding?.root?.findViewById<BaseListComponent>(R.id.comp_list)

        when (getItemViewType(position)) {
            AppConst.VIEWTYPE.VT_FAVORITE_CATEGORY -> {
                val nIndex: Int = getPositionOf(AppConst.VIEWTYPE.VT_FAVORITE_CATEGORY, position)
                Trace.debug(">> VT_FAVORITE_CATEGORY nIndex = $nIndex")
                binding?.setVariable(BR.index, nIndex)
                binding?.setVariable(BR.total_count, getItemCount(AppConst.VIEWTYPE.VT_FAVORITE_CATEGORY))
                baseCompList?.setHeadData(mViewModel.mldForYou.value!!.categoryAlertShow[nIndex])
                baseCompList?.setItemList(mViewModel.mldForYou.value!!.categoryAlertShow[nIndex].categoryItems)
            }

            AppConst.VIEWTYPE.VT_FAVORITE_KEYWORD -> {
                val nIndex: Int = getPositionOf(AppConst.VIEWTYPE.VT_FAVORITE_KEYWORD, position)
                binding?.setVariable(BR.index, nIndex)
                binding?.setVariable(BR.total_count, getItemCount(AppConst.VIEWTYPE.VT_FAVORITE_KEYWORD))
                baseCompList?.setHeadData(mViewModel.mldForYou.value!!.alerts[nIndex])
                baseCompList?.setItemList(mViewModel.mldForYou.value!!.alerts[nIndex].showList)
            }

            AppConst.VIEWTYPE.VT_UPCOMING_HORIZONTAL -> {
//                baseCompList?.setHeadData(mViewModel.mldForYou.value!!.alerts)
                baseCompList?.setItemList(mViewModel.mldForYou.value!!.alertShows)
            }

            AppConst.VIEWTYPE.VT_MY_FAVORITES -> {
//                baseCompList?.setHeadData(mViewModel.mldForYou.value!!.alerts)
                baseCompList?.setItemList(mViewModel.mldForYou.value!!.favorites)
            }

            AppConst.VIEWTYPE.VT_RECENTLY_VIEWED -> {
//                baseCompList?.setHeadData(mViewModel.mldForYou.value!!.alerts)
//                baseCompList?.setItemList(mViewModel.mldForYou.value!!.alerts)
            }

            AppConst.VIEWTYPE.VT_COUPON -> {
//                baseCompList?.setHeadData(mViewModel.mldForYou.value!!.alerts)
//                baseCompList?.setItemList(mViewModel.mldForYou.value!!.alerts)
            }

            else -> { }
        }

        holder.setItemListener()
    }

    override fun getItemCount(): Int {
//        Trace.debug("++ getItemCount()")
        var nCount = 0

        marrViewTypes.forEach {
            nVal -> nCount += getItemCount(nVal)
        }

//        Trace.debug("++ getItemCount() nCount = $nCount")
        return nCount
    }

    fun getItemCount(viewType: Int): Int {
//        Trace.debug("++ getItemCount(viewType)")

        if (mViewModel.mldForYou.value == null) return 0

        return when (viewType) {
            AppConst.VIEWTYPE.VT_FAVORITE_CATEGORY -> {         // not exist
                val nCount = mViewModel.mldForYou.value!!.categoryAlertShow.size
                if (nCount >= 5) 5 else nCount
            }

            AppConst.VIEWTYPE.VT_FAVORITE_KEYWORD -> {          // alert
                val nCount = mViewModel.mldForYou.value!!.alerts.size
                if (nCount >= 5) 5 else nCount
            }

            AppConst.VIEWTYPE.VT_UPCOMING_HORIZONTAL -> {       // not exist
                if (mViewModel.mldForYou.value!!.categoryAlertShow.size > 0) 1 else 0
            }

            AppConst.VIEWTYPE.VT_MY_FAVORITES -> {              // favorite
                if (mViewModel.mldForYou.value!!.favorites.size > 0) 1 else 0
            }

            AppConst.VIEWTYPE.VT_RECENTLY_VIEWED -> 0           // not exist
            AppConst.VIEWTYPE.VT_COUPON -> 0                    // not exist
            else -> 0
        }
    }

    override fun getItemViewType(position: Int): Int {
        var nIndex: Int = 0;

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

    inner class ItemViewHolder<B>(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        fun getBinding(): ViewDataBinding? = DataBindingUtil.getBinding(itemView)

        fun setItemListener() {
            Trace.debug("++ bind()")
            val view: View = itemView.findViewById<ConstraintLayout>(R.id.comp_list)

            if (view is BaseListComponent) {
                Trace.debug(">> view is BaseListComponent")
                view.addItemListener(mListener)
            }
        }
    }
}