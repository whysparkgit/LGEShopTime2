package com.lge.lgshoptimem.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.*
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.BR
import com.lge.lgshoptimem.R
import kotlin.math.abs


class HeaderListComponent @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0):
        BaseListComponent(context, attrs, defStyleAttr)
{
    /** Component ViewModel */
//    private var mViewModel: ComponentViewModel<*> = ApplicationProxy.getInstance().getActivity()!!.run {
//        val viewModel: ComponentViewModel<*> by viewModels()
//        viewModel
//    }
    private val mSnapHelper: SnapHelper = LinearSnapHelper()
    private var mLastX: Float = 0f
    private var mLastY: Float = 0f

    init {
        Trace.debug("++ HeaderListComponent.init() $this")
        /** Attribute RecyclerView Orientaion */
        val nOrientaion: Int
        /** Attribute Index TextView Visibility */
        val bShowIndex: Boolean
        val bShowCount: Boolean

        context.theme.obtainStyledAttributes(attrs, R.styleable.ListComponentAttrs, 0, 0).apply {
            nOrientaion = getInteger(R.styleable.ListComponentAttrs_orientation, 1)
            bShowIndex = getBoolean(R.styleable.ListComponentAttrs_showIndex, false)
            bShowCount = getBoolean(R.styleable.ListComponentAttrs_showCount, false)
            recycle()
        }

        Trace.debug(">> headLayout = $mHeadLayout")
        Trace.debug(">> itemLayout = $mItemLayout")
        Trace.debug(">> nOrientaion = $nOrientaion")
        Trace.debug(">> mstrSubject = $mstrSubject")
        Trace.debug(">> mstrTitle = $mstrTitle")
        Trace.debug(">> mstrSubtitle = $mstrSubtitle")
        Trace.debug(">> bShowIndex = $bShowIndex")
        Trace.debug(">> bShowCount = $bShowCount")
        Trace.debug(">> mstrDataClass = $mstrDataClass")
        Trace.debug(">> mnPosition = $mnPosition")
        Trace.debug(">> mnItemCountLimit = $mnItemCountLimit")

//        var klass: Class<*> = Class.forName(mstrDataClass + ".class")
//        val viewModel = ApplicationProxy.getInstance().getActivity()!!.run {
//            val viewModel = ViewModelProvider(this)[ComponentViewModel<klass::class.java.canonicalName>::class.java]
//            viewModel
//        }

        Trace.debug(">> mBinding.canonicalName = ${mBinding::class.java.canonicalName}")

        findViewById<LinearLayout>(R.id.comp_ll_index)?.visibility = if (bShowIndex) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.comp_tv_item_count)?.visibility = if (bShowCount) View.VISIBLE else View.GONE

        val compRvList: RecyclerView = findViewById(R.id.comp_rv_list)

        Trace.debug(">> R.id.comp_rv_list = ${R.id.comp_rv_list}")
        Trace.debug(">> compRvList = $compRvList")

        compRvList.apply {
            if (nOrientaion == 0) {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            } else {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                mSnapHelper.attachToRecyclerView(this)

                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

                addOnItemTouchListener(object: RecyclerView.OnItemTouchListener {
                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                        Trace.debug(">> onInterceptTouchEvent() action = ${e.action} x = ${e.x} y = ${e.y}")
                        var curX: Float = 0f
                        var curY: Float = 0f
                        var distX: Float = 0f
                        var distY: Float = 0f

                        when (e.action) {
                            MotionEvent.ACTION_DOWN -> {
                                mLastX = e.x
                                mLastY = e.y
                            }

                            MotionEvent.ACTION_MOVE -> {
                                curX = e.x
                                curY = e.y
                                distX = abs(curX - mLastX)
                                distY = abs(curY - mLastY)
                                mLastX = curX
                                mLastY = curY
                                compRvList.parent.requestDisallowInterceptTouchEvent(distX > distY)
                            }
                        }

                        return false
                    }

                    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                        Trace.debug(">> onTouchEvent() action = ${e.action} x = ${e.x} y = ${e.y}")
                    }

                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                        Trace.debug(">> onRequestDisallowInterceptTouchEvent() disallowIntercept = $disallowIntercept")
                    }
                })
            }

//            if (mnItemCountLimit > 0) {
//                setHasFixedSize(true)
//                setItemViewCacheSize(mnItemCountLimit)
//            }

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

    override fun <T> setItemList(items: ArrayList<T>) {
        mItemList = items

        if ((mItemList as ArrayList<*>).size > 0) {
            findViewById<TextView>(R.id.comp_tv_current_index)?.text = "1"
            mBinding.setVariable(BR.total_count, mAdapter.itemCount)
        }

        mAdapter.notifyDataSetChanged()
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

    /** Origin Component Data Query */
//    fun requestList() {
//        val protocol: MainCurationProtocol = ProtocolFactory.create(MainCurationProtocol::class.java)
//
//        protocol.setHttpResponsable(object : HttpResponsable<CurationList.Response> {
//            override fun onResponse(response: CurationList.Response) {
//                Trace.debug(">> requestCuration() onResponse() : $response")
//
////                if (response.isSuccess() && !mViewModel.mldDataList.value.isNullOrEmpty()) {
////                    mViewModel.mldDataList.value = response.data.curations
////                }
//            }
//
//            override fun onFailure(nError: Int, strMsg: String) {
//                Trace.debug(">> requestCuration() onFailure($nError) : $strMsg")
//            }
//        })
//
//        NetworkManager.getInstance().asyncRequest(protocol)
//    }
//
//    class ComponentViewModel<V>: ViewModel()
//    {
//        val mnCurrentIndex: ObservableInt = ObservableInt()
//        val mldDataList: MutableLiveData<ArrayList<V>> = MutableLiveData()
//    }
}
