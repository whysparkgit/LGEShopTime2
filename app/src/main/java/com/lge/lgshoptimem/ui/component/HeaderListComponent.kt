package com.lge.lgshoptimem.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ObservableInt
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
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.model.dto.CurationList
import com.lge.lgshoptimem.model.http.MainCurationProtocol


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

    init {
        Trace.debug("++ HeaderListComponent.init() $this")
        /** Attribute RecyclerView Orientaion */
        val nOrientaion: Int
        /** Attribute Index TextView Visibility */
        val bShowIndex: Boolean

        context.theme.obtainStyledAttributes(attrs, R.styleable.ListComponentAttrs, 0, 0).apply {
            nOrientaion = getInteger(R.styleable.ListComponentAttrs_orientation, 1)
            bShowIndex = getBoolean(R.styleable.ListComponentAttrs_showIndex, true)
            recycle()
        }

        Trace.debug(">> headLayout = $mHeadLayout")
        Trace.debug(">> itemLayout = $mItemLayout")
        Trace.debug(">> nOrientaion = $nOrientaion")
        Trace.debug(">> mstrSubject = $mstrSubject")
        Trace.debug(">> mstrTitle = $mstrTitle")
        Trace.debug(">> mstrSubtitle = $mstrSubtitle")
        Trace.debug(">> bShowIndex = $bShowIndex")
        Trace.debug(">> mstrDataClass = $mstrDataClass")
        Trace.debug(">> mnPosition = $mnPosition")
        Trace.debug(">> mnItemCountLimit = $mnItemCountLimit")

//        var klass: Class<*> = Class.forName(mstrDataClass + ".class")
//        val viewModel = ApplicationProxy.getInstance().getActivity()!!.run {
//            val viewModel = ViewModelProvider(this)[ComponentViewModel<klass::class.java.canonicalName>::class.java]
//            viewModel
//        }

        Trace.debug(">> mBinding.canonicalName = ${mBinding::class.java.canonicalName}")

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
