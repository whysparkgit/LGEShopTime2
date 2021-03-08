package com.lge.lgshoptimem.ui.common

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.model.dto.BaseResponse
import com.lge.lgshoptimem.model.dto.Favorite
import com.lge.lgshoptimem.model.dto.FavoriteProductList
import com.lge.lgshoptimem.model.http.AddFavoriteProductProtocol
import com.lge.lgshoptimem.model.http.DeleteFavoriteProductProtocol
import com.lge.lgshoptimem.model.http.FavoriteProductListProtocol


class FavoriteProductManager
{
    companion object {
        private var mInstance: FavoriteProductManager? = null

        @JvmStatic
        fun getInstance(): FavoriteProductManager = mInstance?:
        synchronized(this) {
            mInstance?: FavoriteProductManager().also {
                mInstance = it
            }
        }
    }

    lateinit var mFavorites: ArrayList<Favorite>

    init {
        requestData()
    }

    fun requestData() {
        val protocol: FavoriteProductListProtocol = ProtocolFactory.create(FavoriteProductListProtocol::class.java)

        protocol.setHttpResponsable(object : HttpResponsable<FavoriteProductList.Response> {
            override fun onResponse(response: FavoriteProductList.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                    mFavorites = response.data.favorites
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }

    fun addFavorite() {
        val protocol: AddFavoriteProductProtocol = ProtocolFactory.create(AddFavoriteProductProtocol::class.java)

        protocol.setHttpResponsable(object : HttpResponsable<BaseResponse> {
            override fun onResponse(response: BaseResponse) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }

    fun deleteFavorite() {
        val protocol: DeleteFavoriteProductProtocol = ProtocolFactory.create(DeleteFavoriteProductProtocol::class.java)

        protocol.setHttpResponsable(object : HttpResponsable<BaseResponse> {
            override fun onResponse(response: BaseResponse) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }
}