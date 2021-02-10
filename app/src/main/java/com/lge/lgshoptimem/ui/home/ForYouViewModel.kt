package com.lge.lgshoptimem.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.model.dto.Curation
import com.lge.lgshoptimem.model.dto.CurationList
import com.lge.lgshoptimem.model.dto.ForYou
import com.lge.lgshoptimem.model.http.ForYouProtocol
import com.lge.lgshoptimem.model.http.MainCurationProtocol
import com.lge.lgshoptimem.ui.component.BaseListComponent

class ForYouViewModel: ViewModel() {
    val mldForYou: MutableLiveData<ForYou.Response.Data> = MutableLiveData()

    inline fun <reified T> checkType(klass: Any) = (klass is T)
//        return when (T::class) {
//            Curation::class -> { mldDataList.value as T }
//            else -> { mldDataList.value as T }
//        }
//    }

    /** Custom Component Data Query */
    fun requestData() {
        val protocol: ForYouProtocol = ProtocolFactory.create(ForYouProtocol::class.java)

        protocol.setHttpResponsable(object : HttpResponsable<ForYou.Response> {
            override fun onResponse(response: ForYou.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                    mldForYou.value = response.data
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }
}