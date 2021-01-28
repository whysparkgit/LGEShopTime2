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

    private val mListener: ComponentItemListener = mFragment as ComponentItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForYouListAdapter.ItemViewHolder<*> {
//        Trace.debug("++ onCreateViewHolder() viewType = $viewType")
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL -> {
                val binding: ViewNextUpcomingHorizontalBinding = DataBindingUtil.inflate(inflater, R.layout.view_next_upcoming_horizontal, parent, false)
                ItemViewHolder<ViewNextUpcomingHorizontalBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_CATEGORY_REMINDER -> {
                val binding: ViewCategoryReminderBinding = DataBindingUtil.inflate(inflater, R.layout.view_category_reminder, parent, false)
                ItemViewHolder<ViewCategoryReminderBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_KEYWORD_REMINDER -> {
                val binding: ViewKeywordReminderBinding = DataBindingUtil.inflate(inflater, R.layout.view_keyword_reminder, parent, false)
                ItemViewHolder<ViewKeywordReminderBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_VERTICAL -> {
                val binding: ViewNextUpcomingVerticalBinding = DataBindingUtil.inflate(inflater, R.layout.view_next_upcoming_vertical, parent, false)
                ItemViewHolder<ViewNextUpcomingVerticalBinding>(binding.root)
            }

            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> {
                val binding: ViewProductListBinding = DataBindingUtil.inflate(inflater, R.layout.view_product_list, parent, false)
                ItemViewHolder<ViewProductListBinding>(binding.root)
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
                val binding: ViewProductListBinding = DataBindingUtil.inflate(inflater, R.layout.view_product_list, parent, false)
                ItemViewHolder<ViewProductListBinding>(binding.root)
            }
        }
    }

    override fun onBindViewHolder(holder: ForYouListAdapter.ItemViewHolder<*>, position: Int) {
//        Trace.debug("++ onBindViewHolder()")
        val binding: ViewDataBinding? = holder.getBinding()
        binding?.setVariable(BR.position, position)
        binding?.setVariable(BR.listener, mListener)

        // fixme setViewModel by ItemView each
        if (!mViewModel.mldDataList.value.isNullOrEmpty()) {
            val baseCompList = binding?.root?.findViewById<BaseListComponent>(R.id.comp_list)

//            baseCompList?.setViewModel(mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size] as Any)
//            baseCompList?.setViewModel<Curation, Product>(mViewModel)
            baseCompList?.setHeadData(mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size])
            baseCompList?.setItemList(mViewModel.mldDataList.value!![position % mViewModel.mldDataList.value!!.size].productInfos)
        }

        holder.bind()
    }

    override fun getItemCount(): Int {
//        Trace.debug("++ getItemCount()")
        var nCount = 0

        var arrViewTypes = arrayOf(
                AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL,
                AppConst.VIEWTYPE.VT_CATEGORY_REMINDER,
                AppConst.VIEWTYPE.VT_KEYWORD_REMINDER,
                AppConst.VIEWTYPE.VT_NEXT_UPCOMING_VERTICAL,
                AppConst.VIEWTYPE.VT_LIVE_CHANNELS,
                AppConst.VIEWTYPE.VT_MY_FAVORITES,
                AppConst.VIEWTYPE.VT_RECENTLY_VIEWED,
                AppConst.VIEWTYPE.VT_COUPON
        )

        arrViewTypes.forEach {
            nVal -> nCount += getItemCount(nVal)
        }

//        Trace.debug("++ getItemCount() nCount = $nCount")
        return nCount
    }

    fun getItemCount(viewType: Int): Int {
//        Trace.debug("++ getItemCount(viewType)")
        return when (viewType) {
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL -> 1
            AppConst.VIEWTYPE.VT_CATEGORY_REMINDER -> 1
            AppConst.VIEWTYPE.VT_KEYWORD_REMINDER -> 1
            AppConst.VIEWTYPE.VT_NEXT_UPCOMING_VERTICAL -> 1
            AppConst.VIEWTYPE.VT_LIVE_CHANNELS -> 1
            AppConst.VIEWTYPE.VT_MY_FAVORITES -> 1
            AppConst.VIEWTYPE.VT_RECENTLY_VIEWED -> 1
            AppConst.VIEWTYPE.VT_COUPON -> 1
            else -> 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        var nIndex: Int = 0;
        var arrViewTypes = arrayOf(
                AppConst.VIEWTYPE.VT_NEXT_UPCOMING_HORIZONTAL,
                AppConst.VIEWTYPE.VT_CATEGORY_REMINDER,
                AppConst.VIEWTYPE.VT_KEYWORD_REMINDER,
                AppConst.VIEWTYPE.VT_NEXT_UPCOMING_VERTICAL,
                AppConst.VIEWTYPE.VT_LIVE_CHANNELS,
                AppConst.VIEWTYPE.VT_MY_FAVORITES,
                AppConst.VIEWTYPE.VT_RECENTLY_VIEWED,
                AppConst.VIEWTYPE.VT_COUPON
        )

        arrViewTypes.forEach {
            nVal -> if (position in nIndex until nIndex + getItemCount(nVal)) {
//                Trace.debug("++ getItemViewType() position = $position ViewType = $nVal")
                return nVal
            }

            nIndex += getItemCount(nVal)
        }

        return arrViewTypes.last()
    }

    inner class ItemViewHolder<B>(itemView: View) :
            RecyclerView.ViewHolder(itemView)
    {
        fun getBinding(): ViewDataBinding? = DataBindingUtil.getBinding(itemView)

        fun bind() {
            Trace.debug("++ bind()")
            val view: View = itemView.findViewById<ConstraintLayout>(R.id.comp_list)

            if (view is BaseListComponent) {
                Trace.debug(">> view is BaseListComponent")
                view.addItemListener(mListener)
            }
        }
    }
}