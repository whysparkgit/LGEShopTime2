package com.lge.lgshoptimem.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.model.dto.HotPicks
import com.lge.lgshoptimem.model.http.HotPicksProtocol

class HotPicksViewModel : ViewModel() {
    val mldHotPicks: MutableLiveData<HotPicks.Response.Data> = MutableLiveData()

    /** Custom Component Data Query */
    fun requestData() {
        val protocol: HotPicksProtocol = ProtocolFactory.create(HotPicksProtocol::class.java)

        protocol.setHttpResponsable(object : HttpResponsable<HotPicks.Response> {
            override fun onResponse(response: HotPicks.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                    mldHotPicks.value = response.data
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }
}