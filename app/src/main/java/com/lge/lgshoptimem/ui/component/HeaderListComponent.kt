package com.lge.lgshoptimem.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableInt
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.BR
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.model.dto.CurationList
import com.lge.lgshoptimem.model.http.MainCurationProtocol


class HeaderListComponent @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0):
        ConstraintLayout(context, attrs, defStyleAttr), ComponentItemListener
{
    /** Attribute Main View Layout */
    private var mHeadLayout: Int = 0
    /** Attribute Item View Layout */
    private var mItemLayout: Int = 0
    private var mstrDataClass: String? = null
    /** Component XML Binding */
    private val mBinding: ViewDataBinding
    /** Component Adapter */
    private val mAdapter: ComponentAdapter
    /** Component ViewModel */
//    private var mViewModel: ComponentViewModel<*> = ApplicationProxy.getInstance().getActivity()!!.run {
//        val viewModel: ComponentViewModel<*> by viewModels()
//        viewModel
//    }
    private var mstrTitle: String?
    var mnItemCountLimit = -1
    private val mSnapHelper: SnapHelper = LinearSnapHelper()
    private var mItemListener:ComponentItemListener? = null

    init {
        Trace.debug("++ HeaderListComponent.init() $this")
        /** Attribute RecyclerView Orientaion */
        val nOrientaion: Int
        /** Attribute Index TextView Visibility */
        val bShowIndex: Boolean

        context.theme.obtainStyledAttributes(attrs, R.styleable.ListComponentAttrs, 0, 0).apply {
            mHeadLayout = getResourceId(R.styleable.ListComponentAttrs_headLayout, 0)
            mItemLayout = getResourceId(R.styleable.ListComponentAttrs_itemLayout, 0)
            nOrientaion = getInteger(R.styleable.ListComponentAttrs_orientation, 1)
            mstrTitle = getString(R.styleable.ListComponentAttrs_title)
            bShowIndex = getBoolean(R.styleable.ListComponentAttrs_showIndex, true)
            mstrDataClass = getString(R.styleable.ListComponentAttrs_itemDataClass)
            mnItemCountLimit = getInteger(R.styleable.ListComponentAttrs_itemCount, -1)
            recycle()
        }

        Trace.debug(">> headLayout = $mHeadLayout")
        Trace.debug(">> itemLayout = $mItemLayout")
        Trace.debug(">> nOrientaion = $nOrientaion")
        Trace.debug(">> mstrTitle = $mstrTitle")
        Trace.debug(">> bShowIndex = $bShowIndex")
        Trace.debug(">> mstrDataClass = $mstrDataClass")
        Trace.debug(">> mnItemCountLimit = $mnItemCountLimit")

//        var klass: Class<*> = Class.forName(mstrDataClass + ".class")
//        val viewModel = ApplicationProxy.getInstance().getActivity()!!.run {
//            val viewModel = ViewModelProvider(this)[ComponentViewModel<klass::class.java.canonicalName>::class.java]
//            viewModel
//        }

//        mBinding = inflate(LayoutInflater.from(context), this, true)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), mHeadLayout, this, true)
        mAdapter = ComponentAdapter(this)

        Trace.debug(">> mBinding.canonicalName = ${mBinding::class.java.canonicalName}")

        mBinding.setVariable(BR.component, this)
        mBinding.setVariable(BR.adapter, mAdapter)

        findViewById<TextView>(R.id.comp_tv_title).visibility = if (mstrTitle.isNullOrEmpty()) View.GONE else View.VISIBLE
        findViewById<LinearLayout>(R.id.comp_ll_index).visibility = if (bShowIndex) View.VISIBLE else View.GONE

        val compRvList: RecyclerView = findViewById(R.id.comp_rv_list)

        Trace.debug(">> R.id.comp_rv_list = ${R.id.comp_rv_list}")
        Trace.debug(">> compRvList = $compRvList")

        compRvList.apply {
            if (nOrientaion == 0) {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            } else {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                mSnapHelper.attachToRecyclerView(this)

                addOnItemTouchListener(object: RecyclerView.OnItemTouchListener {
                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        when (e.action) {
                            MotionEvent.ACTION_MOVE -> compRvList.parent.requestDisallowInterceptTouchEvent(true)
                        }

                        return false
                    }

                    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                    }

                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                    }
                })
            }

            adapter = mAdapter

//            val lp: ConstraintLayout.LayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
//            layoutParams.apply {
//                Trace.debug(">> compRvList.height = $height")
//                Trace.debug(">> compRvList.layoutParams = $layoutParams")
//            }
        }

        compRvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val pos: Int = mSnapHelper.getSnapPosition(recyclerView) + 1
                Trace.debug("++ onScrollStateChanged() pos = $pos newState = $newState")
                findViewById<TextView>(R.id.comp_tv_current_index).text = pos.toString()
//                mViewModel.mnCurrentIndex.set(pos)
            }
        })

//        mViewModel.mldDataList.observeForever(this::onDataListChanged)
//
//        mViewModel.mnCurrentIndex.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
//            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
//                val value: Int = (sender as ObservableInt).get()
//                Trace.debug("++ onPropertyChanged() value = $value propertyId = $propertyId this=$this")
//                findViewById<TextView>(R.id.comp_tv_current_index).text = value.toString()
//            }
//        })
    }

    override fun onFinishInflate() {
        Trace.debug("++ onFinishInflate()")
        super.onFinishInflate()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Trace.debug("++ onLayout() l=$l t=$t r=$r b=$b root = ${mBinding.root}")
        mBinding.root.layout(l, t, r, b)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Trace.debug("++ onMeasure() widthMeasureSpec = $widthMeasureSpec heightMeasureSpec = $heightMeasureSpec")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun addItemListener(l: ComponentItemListener) {
        mItemListener = l
    }

    private fun onDataListChanged(itemList: ArrayList<*>) {
        Trace.debug("++ onDataListChanged() itemDataClass = ${itemList[0]::class.java.canonicalName}")

        if (itemList[0].javaClass.canonicalName == mstrDataClass) {
            Trace.debug(">> canonicalName == mstrDataClass")
            findViewById<TextView>(R.id.comp_tv_total_count).text = itemList.size.toString()
            mAdapter.notifyDataSetChanged()
        }
    }

    fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager?: return 0   //RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager)?: return 0   //RecyclerView.NO_POSITION

        return layoutManager.getPosition(snapView)
    }

    fun setTitle(strTitle: String) {
        Trace.debug("++ setTitle()")
        mstrTitle = strTitle
        findViewById<TextView>(R.id.comp_tv_title).visibility = if (mstrTitle.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    fun getTitle() = mstrTitle

    fun refresh() {
        Trace.debug(">> mBinding.root = ${mBinding.root}")
        Trace.debug(">> comp_cl_header = ${findViewById<ConstraintLayout>(R.id.comp_cl_header)}")
        Trace.debug(">> comp_rv_list = ${findViewById<RecyclerView>(R.id.comp_rv_list)}")
        mAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View, pos: Int) {
        Trace.debug("++ onClick() v = ${v.id} pos = $pos")
        mItemListener?.onClick(v, pos)
    }

    /** Origin Component Data Query */
    fun requestList() {
        val protocol: MainCurationProtocol = ProtocolFactory.create(MainCurationProtocol::class.java)

        protocol.setHttpResponsable(object : HttpResponsable<CurationList.Response> {
            override fun onResponse(response: CurationList.Response) {
                Trace.debug(">> requestCuration() onResponse() : $response")

//                if (response.isSuccess() && !mViewModel.mldDataList.value.isNullOrEmpty()) {
//                    mViewModel.mldDataList.value = response.data.curations
//                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestCuration() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }

    inner class ComponentAdapter(val component: HeaderListComponent):
            RecyclerView.Adapter<ComponentAdapter.ItemViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentAdapter.ItemViewHolder {
            Trace.debug("++ onCreateViewHolder() parent = $parent")
            val inflater = LayoutInflater.from(parent.context)
            /** Component Item XML Binding */
//            val binding = U.inflate(inflater, parent, false)
            val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, mItemLayout, parent, false)
            Trace.debug("++ onCreateViewHolder() binding = $binding")

            return ItemViewHolder(binding.root)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            Trace.debug("++ onBindViewHolder() position = $position this = $this")
            val binding: ViewDataBinding = holder.getBinding()!!
            binding.setVariable(BR.position, position)
            binding.setVariable(BR.listener, component)
//            binding.setVariable(BR.viewmodel, mViewModel)

//            val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
//            val compRvList: RecyclerView = mBinding.root.findViewById(R.id.comp_rv_list)
//            lp.topToBottom = mBinding.root.findViewById<ConstraintLayout>(R.id.comp_cl_header).id
//            lp.height = holder.itemView.layoutParams.height
//            compRvList.layoutParams = lp
        }

        override fun getItemCount(): Int {
            if (mnItemCountLimit > -1) {
                Trace.debug("++ getItemCount() mnItemCountLimit = $mnItemCountLimit this = $this")
                return mnItemCountLimit
            }

//            var nCount = if (mViewModel.mldDataList.value.isNullOrEmpty()) {
//                0
//            } else {
//                mViewModel.mldDataList.value!!.size
//            }

//            Trace.debug("++ getItemCount() nCount = $nCount")
//            return nCount
            return 0;
        }

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun getBinding() = DataBindingUtil.getBinding<ViewDataBinding>(itemView)
        }
    }

    class ComponentViewModel<V>: ViewModel()
    {
        val mnCurrentIndex: ObservableInt = ObservableInt()
        val mldDataList: MutableLiveData<ArrayList<V>> = MutableLiveData()
    }
}
