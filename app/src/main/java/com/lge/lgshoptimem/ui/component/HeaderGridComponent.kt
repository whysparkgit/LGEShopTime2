package com.lge.lgshoptimem.ui.component

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableInt
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.model.dto.CurationList
import com.lge.lgshoptimem.model.http.MainCurationProtocol


class HeaderGridComponent @JvmOverloads constructor(
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
    /** Component Grid Row Count */
    private var mnRowCount = 2

    init {
        Trace.debug("++ HeaderGridComponent.init() $this")

        context.theme.obtainStyledAttributes(attrs, R.styleable.ListComponentAttrs, 0, 0).apply {
            mnRowCount = getInteger(R.styleable.ListComponentAttrs_rowCount, 2)
            recycle()
        }

        Trace.debug(">> headLayout = $mHeadLayout")
        Trace.debug(">> itemLayout = $mItemLayout")
        Trace.debug(">> mstrSubject = $mstrSubject")
        Trace.debug(">> mstrTitle = $mstrTitle")
        Trace.debug(">> mstrSubtitle = $mstrSubtitle")
        Trace.debug(">> mstrDataClass = $mstrDataClass")
        Trace.debug(">> mnRowCount = $mnRowCount")
        Trace.debug(">> mnPosition = $mnPosition")
        Trace.debug(">> mnItemCountLimit = $mnItemCountLimit")

//        var klass: Class<*> = Class.forName(mstrDataClass + ".class")
//        val viewModel = ApplicationProxy.getInstance().getActivity()!!.run {
//            val viewModel = ViewModelProvider(this)[ComponentViewModel<klass::class.java.canonicalName>::class.java]
//            viewModel
//        }

        Trace.debug(">> mBinding.canonicalName = ${mBinding::class.java.canonicalName}")

        val compRvList: RecyclerView = findViewById(R.id.comp_rv_list)

        Trace.debug(">> R.id.comp_rv_list = ${R.id.comp_rv_list}")
        Trace.debug(">> compRvList = $compRvList")

        compRvList.apply {
            layoutManager = GridLayoutManager(context, mnRowCount, GridLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

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

    override var mfunUpdate: ((ViewDataBinding) -> Unit)? = {
        val nWidth: Int = Resources.getSystem().displayMetrics.widthPixels;
        val lp: LayoutParams = LayoutParams(nWidth / mnRowCount, LayoutParams.WRAP_CONTENT)
        it.root.layoutParams = lp
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

//    class ComponentViewModel<V>: ViewModel()
//    {
//        val mnCurrentIndex: ObservableInt = ObservableInt()
//        val mldDataList: MutableLiveData<ArrayList<V>> = MutableLiveData()
//    }
}
