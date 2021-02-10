package com.lge.lgshoptimem.ui.product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.model.dto.ProductDetail
import com.lge.lgshoptimem.model.dto.WatchNow
import com.lge.lgshoptimem.model.http.ProductDetailProtocol
import com.lge.lgshoptimem.model.http.WatchNowProtocol

class DetailViewModel : ViewModel() {
    val mldDetail: MutableLiveData<ProductDetail.Response.Data> = MutableLiveData()

    /** Custom Component Data Query */
    fun requestData(partnerId: String, productId: String, curationId: String = "") {
        val protocol: ProductDetailProtocol = ProtocolFactory.create(ProductDetailProtocol::class.java)

        protocol.setPartnerId(partnerId)
        protocol.setProductId(productId)
        protocol.setCurationId(curationId)

        protocol.setHttpResponsable(object : HttpResponsable<ProductDetail.Response> {
            override fun onResponse(response: ProductDetail.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                    mldDetail.value = response.data
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }
}