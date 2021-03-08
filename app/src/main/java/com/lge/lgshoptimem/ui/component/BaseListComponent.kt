package com.lge.lgshoptimem.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.BR
import com.lge.lgshoptimem.R
import kotlin.reflect.KClass


open class BaseListComponent @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0):
        ConstraintLayout(context, attrs, defStyleAttr), ComponentItemListener
{
    /** Attribute Main View Layout */
    var mHeadLayout: Int = 0
    /** Attribute Item View Layout */
    var mItemLayout: Int = 0
    var mstrDataClass: String? = null
    /** Component XML Binding */
    val mBinding: ViewDataBinding
    /** Component Adapter */
    val mAdapter: ComponentAdapter
    /** Component ViewModel */
    lateinit var mViewModel: ComponentViewModel
    var mItemList: ArrayList<*>? = null
//    private var mViewModel: ComponentViewModel<*> = ApplicationProxy.getInstance().getActivity()!!.run {
//        val viewModel: ComponentViewModel<*> by viewModels()
//        viewModel
//    }
    /** Component Subject */
    var mstrSubject: String? = null
    /** Component Title */
    var mstrTitle: String? = null
    /** Component Subtitle */
    var mstrSubtitle: String? = null
    /** Component Position */
    var mnPosition: Int = 0
    var mnItemCountLimit = -1
    var mItemListener:ComponentItemListener? = null
    open var mfunUpdate: ((ViewDataBinding) -> Unit)? = null

    init {
        Trace.debug("++ BaseListComponent.init()")

        context.theme.obtainStyledAttributes(attrs, R.styleable.ListComponentAttrs, 0, 0).apply {
            mHeadLayout = getResourceId(R.styleable.ListComponentAttrs_headLayout, 0)
            mItemLayout = getResourceId(R.styleable.ListComponentAttrs_itemLayout, 0)
            mstrSubject = getString(R.styleable.ListComponentAttrs_subject)
            mstrTitle = getString(R.styleable.ListComponentAttrs_title)
            mstrSubtitle = getString(R.styleable.ListComponentAttrs_subtitle)
            mstrDataClass = getString(R.styleable.ListComponentAttrs_itemDataClass)
            mnPosition = getInteger(R.styleable.ListComponentAttrs_position, 0)
            mnItemCountLimit = getInteger(R.styleable.ListComponentAttrs_itemCount, -1)
        }

//        Trace.debug(">> headLayout = $mHeadLayout")
//        Trace.debug(">> itemLayout = $mItemLayout")
//        Trace.debug(">> mstrSubject = $mstrSubject")
//        Trace.debug(">> mstrTitle = $mstrTitle")
//        Trace.debug(">> mstrSubtitle = $mstrSubtitle")
//        Trace.debug(">> mstrDataClass = $mstrDataClass")
//        Trace.debug(">> mnPosition = $mnPosition")
        Trace.debug(">> mnItemCountLimit = $mnItemCountLimit")

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), mHeadLayout, this, true)
        mAdapter = ComponentAdapter(this)

        Trace.debug(">> mBinding.canonicalName = ${mBinding::class.java.canonicalName}")

        mBinding.setVariable(BR.component, this)
        mBinding.setVariable(BR.adapter, mAdapter)

        findViewById<TextView>(R.id.comp_tv_subject)?.visibility = if (mstrSubject.isNullOrEmpty()) View.GONE else View.VISIBLE
        findViewById<TextView>(R.id.comp_tv_title)?.visibility = if (mstrTitle.isNullOrEmpty()) View.GONE else View.VISIBLE
        findViewById<TextView>(R.id.comp_tv_subtitle)?.visibility = if (mstrSubtitle.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun onFinishInflate() {
        Trace.debug("++ onFinishInflate()")
        super.onFinishInflate()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Trace.debug("++ onLayout() l=$l t=$t r=$r b=$b root = ${mBinding.root}")
        super.onLayout(changed, l, t, r, b)
//        mBinding.root.layout(l, t, r, b)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Trace.debug("++ onMeasure() widthMeasureSpec = $widthMeasureSpec heightMeasureSpec = $heightMeasureSpec")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun addItemListener(l: ComponentItemListener) {
        mItemListener = l
    }

    fun setSubject(strSubject: String) {
        Trace.debug("++ setSubject() strSubject = $strSubject")
        mstrSubject = strSubject

        if (mstrSubject.isNullOrEmpty()) {
            findViewById<TextView>(R.id.comp_tv_subject)?.visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.comp_tv_subject)?.visibility =  View.VISIBLE
            findViewById<TextView>(R.id.comp_tv_subject)?.text = mstrSubject
        }
    }

    fun setTitle(strTitle: String) {
        Trace.debug("++ setTitle() strTitle = $strTitle")
        mstrTitle = strTitle

        if (mstrTitle.isNullOrEmpty()) {
            findViewById<TextView>(R.id.comp_tv_title)?.visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.comp_tv_title)?.visibility =  View.VISIBLE
            findViewById<TextView>(R.id.comp_tv_title)?.text = mstrTitle
        }
    }

    fun setSubtitle(strSubtitle: String) {
        Trace.debug("++ setSubtitle() strSubtitle = $strSubtitle")
        mstrSubtitle = strSubtitle

        if (mstrSubtitle.isNullOrEmpty()) {
            findViewById<TextView>(R.id.comp_tv_subtitle)?.visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.comp_tv_subtitle)?.visibility =  View.VISIBLE
            findViewById<TextView>(R.id.comp_tv_subtitle)?.text = mstrSubtitle
        }
    }

    fun getSubject() = mstrSubject

    fun getTitle() = mstrTitle

    fun getSubtitle() = mstrSubtitle

    fun setPosition(pos: Int) {
        mnPosition = pos
    }

    fun getPosition() = mnPosition

    fun setItemCountLimit(nCount: Int) {
        mnItemCountLimit = nCount
    }

    fun getItemCountLimit() = mnItemCountLimit

    fun setViewModel(viewModel: Any) {
        Trace.debug(">> setViewModel()")
//        mViewModel = viewModel
        mBinding.setVariable(BR.viewmodel, viewModel)
        mAdapter.notifyDataSetChanged()
    }

    fun <H, I> setViewModel(viewModel: ComponentViewModel) {
        Trace.debug(">> setViewModel()")
        mViewModel = viewModel

        mBinding.setVariable(BR.viewdata, mViewModel.getHeadData<H>())
        mItemList = mViewModel.getItemList<I>()

        mAdapter.notifyDataSetChanged()
    }

    fun <T> setHeadData(data: T) {
        mBinding.setVariable(BR.viewdata, data)
    }

    open fun <T> setItemList(items: ArrayList<T>) {
        mItemList = items
        mAdapter.notifyDataSetChanged()
//        mBinding.setVariable(BR.adapter, mAdapter)
    }

    fun getViewModel() = mViewModel

    fun refresh() {
        Trace.debug("++ refresh()")
//        Trace.debug(">> mBinding.root = ${mBinding.root}")
//        Trace.debug(">> comp_cl_header = ${findViewById<ConstraintLayout>(R.id.comp_cl_header)}")
//        Trace.debug(">> comp_rv_list = ${findViewById<RecyclerView>(R.id.comp_rv_list)}")
        mAdapter.notifyDataSetChanged()
//        mBinding.setVariable(BR.adapter, mAdapter)
    }

    fun refreshItem(position: Int) {
        mAdapter.notifyItemChanged(position)
    }

    override fun onClick(v: View, pos: Int) {
//        Trace.debug("++ onClick() v = ${v.id} pos = $pos")
        mItemListener?.onItemClick(mBinding.root, mnPosition, v, pos)
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
        Trace.debug("++ onItemClick() parent = ${parent.id} parentPos = $parentPos item = ${item.id} pos = $pos")
    }

    open inner class ComponentAdapter(open val component: BaseListComponent):
            RecyclerView.Adapter<ComponentAdapter.ItemViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentAdapter.ItemViewHolder {
            Trace.debug("++ onCreateViewHolder() parent = $parent")
            val inflater = LayoutInflater.from(parent.context)
            /** Component Item XML Binding */
            val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, mItemLayout, parent, false)
            mfunUpdate?.invoke(binding)

            Trace.debug("++ onCreateViewHolder() binding = $binding")
            return ItemViewHolder(binding.root)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val binding: ViewDataBinding = holder.getBinding()!!
            Trace.debug("++ onBindViewHolder() position = $position binding = $binding")
            binding.setVariable(BR.position, position)
            binding.setVariable(BR.listener, component)

            if (!mItemList.isNullOrEmpty() && mItemList!!.size > position) {
                binding.setVariable(BR.viewdata, mItemList?.get(position))
            }
//            else {
//                // fixme
//                binding.setVariable(BR.viewdata, mItemList?.last())
//            }

//            val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
//            val compRvList: RecyclerView = mBinding.root.findViewById(R.id.comp_rv_list)
//            lp.topToBottom = mBinding.root.findViewById<ConstraintLayout>(R.id.comp_cl_header).id
//            lp.height = holder.itemView.layoutParams.height
//            compRvList.layoutParams = lp
        }

        override fun getItemCount(): Int {
            Trace.debug("++ getItemCount() mnItemCountLimit = $mnItemCountLimit")   // itemListSize = ${mItemList!!.size}")

            val nCount = if (mItemList.isNullOrEmpty()) {
                0
            } else if (mnItemCountLimit == -1) {
                mItemList!!.size
            } else if (mItemList!!.size > mnItemCountLimit) {
                mnItemCountLimit
            } else {
                mItemList!!.size
            }

            Trace.debug("++ getItemCount() nCount = $nCount")
            return nCount
        }

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun getBinding() = DataBindingUtil.getBinding<ViewDataBinding>(itemView)
        }
    }

    interface ComponentViewModel
    {
//        val mnCurrentIndex: ObservableInt = ObservableInt()
//        val mldDataList: MutableLiveData<ArrayList<V>> = MutableLiveData()

        fun <H> getHeadData(): H

        fun <I> getItemList(): ArrayList<I>
    }
}
