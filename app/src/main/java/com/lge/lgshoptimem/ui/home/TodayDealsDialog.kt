package com.lge.lgshoptimem.ui.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import com.lge.core.app.ApplicationProxy
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.databinding.DialogTodayDealsBinding
import com.lge.lgshoptimem.model.dto.Curation
import com.lge.lgshoptimem.model.dto.CurationList
import com.lge.lgshoptimem.model.http.MainCurationProtocol
import com.lge.lgshoptimem.ui.component.BaseListComponent
import com.lge.lgshoptimem.ui.component.HeaderListComponent

class TodayDealsDialog: DialogFragment()
{
    private lateinit var mBinding: DialogTodayDealsBinding
//    private val mViewModel = ApplicationProxy.getInstance().getActivity()!!.run {
//        val viewModel: BaseListComponent.ComponentViewModel<Curation> by viewModels()
//        viewModel
//    }

    override fun onAttach(context: Context) {
        Trace.debug("++ onAttach()")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Trace.debug("++ onCreate()")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Trace.debug("++ onCreateDialog()")
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        Trace.debug("++ onCreateView()")

        mBinding = DialogTodayDealsBinding.inflate(inflater)

        mBinding.incTodayDeals.compList.apply {
            setTitle("TODAY'S DEALS")
//            requestList()
        }

        mBinding.incTodayDeals.compList.requestData()

        return mBinding.root
    }

    /** Custom Component Data Query */
    fun HeaderListComponent.requestData() {
        val protocol: MainCurationProtocol = ProtocolFactory.create(MainCurationProtocol::class.java)

        protocol.setHttpResponsable(object : HttpResponsable<CurationList.Response> {
            override fun onResponse(response: CurationList.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
//                    mViewModel.mldDataList.value = response.data.curations
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }
}