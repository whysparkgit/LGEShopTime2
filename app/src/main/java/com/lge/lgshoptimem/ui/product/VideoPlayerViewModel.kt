package com.lge.lgshoptimem.ui.product

import androidx.databinding.ObservableInt
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
import kotlin.properties.ObservableProperty

class VideoPlayerViewModel : ViewModel() {
    val mldVideoProduct: MutableLiveData<WatchNow.Response.Data> = MutableLiveData()

    /** Custom Component Data Query */
    fun requestData(video: Video) {
        val protocol: WatchNowProtocol = ProtocolFactory.create(WatchNowProtocol::class.java)
        protocol.setPartnerId(video.patnrId)
        protocol.setShowId(video.showId)

        protocol.setHttpResponsable(object : HttpResponsable<WatchNow.Response> {
            override fun onResponse(response: WatchNow.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                    mldVideoProduct.value = response.data
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }
}