package com.lge.lgshoptimem.ui.home

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.model.dto.Curation
import com.lge.lgshoptimem.model.dto.CurationList
import com.lge.lgshoptimem.model.http.MainCurationProtocol

class HotPicksViewModel : ViewModel() {
    val mldDataList: MutableLiveData<ArrayList<Curation>> = MutableLiveData()

    /** Custom Component Data Query */
    fun requestData() {
        val protocol: MainCurationProtocol = ProtocolFactory.create(MainCurationProtocol::class.java)

        protocol.setHttpResponsable(object : HttpResponsable<CurationList.Response> {
            override fun onResponse(response: CurationList.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                    mldDataList.value = response.data.curations
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }
}