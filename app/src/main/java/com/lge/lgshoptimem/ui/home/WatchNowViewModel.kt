package com.lge.lgshoptimem.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.model.dto.Video
import com.lge.lgshoptimem.model.dto.WatchNow
import com.lge.lgshoptimem.model.http.WatchNowProtocol
import com.lge.lgshoptimem.ui.common.CommonData

class WatchNowViewModel : ViewModel() {
    val mldWatchNow: MutableLiveData<WatchNow.Response.Data> = MutableLiveData()

    /** Custom Component Data Query */
    fun requestData(partnerId: String, showId: String) {
        val protocol: WatchNowProtocol = ProtocolFactory.create(WatchNowProtocol::class.java)
        protocol.setPartnerId(partnerId)
        protocol.setShowId(showId)

        protocol.setHttpResponsable(object : HttpResponsable<WatchNow.Response> {
            override fun onResponse(response: WatchNow.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                    mldWatchNow.value = response.data
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }
}