package com.lge.lgshoptimem.ui.common

import android.widget.Toast
import com.lge.core.app.ApplicationProxy
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.model.dto.*
import com.lge.lgshoptimem.model.http.FavoriteProductAddProtocol
import com.lge.lgshoptimem.model.http.FavoriteProductDeleteProtocol
import com.lge.lgshoptimem.model.http.FavoriteProductListProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FavoriteProductManager
{
    companion object {
        private var mInstance: FavoriteProductManager? = null
        private const val SYNC_COUNT_MAX = 1

        @JvmStatic
        fun getInstance(): FavoriteProductManager = mInstance?:
        synchronized(this) {
            mInstance?: FavoriteProductManager().also {
                mInstance = it
            }
        }
    }

    var mFavorites: ArrayList<Product> = arrayListOf()
    var mnSyncCount = 0

    init {
        requestData()
    }

    fun isFavoriteChecked(productId: String): Boolean {
        var bRet = false

        mFavorites.forEach {
            if (it.prdtId == productId) bRet = it.bFavorite
        }

        Trace.debug("++ isFavoriteChecked() productId = $productId checked = $bRet")

        return bRet
    }

    fun addFavorite(product: Product) {
        Trace.debug("++ addFavorite() productId = ${product.prdtId} mnSyncCount = $mnSyncCount")

        mnSyncCount++

        mFavorites.forEach {
            if (it.prdtId == product.prdtId) {
                it.bFavorite = true
                if (mnSyncCount >= SYNC_COUNT_MAX) syncData()
                return
            }
        }

        product.bFavorite = true
        mFavorites.add(product)

        if (mnSyncCount >= SYNC_COUNT_MAX) syncData()
    }

    fun deleteFavorite(product: Product) {
        Trace.debug("++ deleteFavorite() productId = ${product.prdtId} mnSyncCount = $mnSyncCount")

        mnSyncCount++

        for (favorite: Product in mFavorites) {
            if (favorite.prdtId == product.prdtId) {
                favorite.bFavorite = false
                break
            }
        }

        if (mnSyncCount >= SYNC_COUNT_MAX) syncData()
    }

    fun getFavoriteProducts() = mFavorites

    fun syncData() {
        Trace.debug("++ syncData() favorite sync")
        mnSyncCount = 0

        CoroutineScope(Dispatchers.IO).launch {
            requestDeleteFavorite()
            requestAddFavorite()

            val iterator = mFavorites.iterator()

            while (iterator.hasNext()) {
                val product = iterator.next()

                if (product.bFavorite) {
                    product.bOrigin = true
                } else {
                    iterator.remove()
                }
            }
        }
    }

    fun requestData() {
        val protocol: FavoriteProductListProtocol = ProtocolFactory.create(FavoriteProductListProtocol::class.java)

        protocol.setHttpResponsable(object : HttpResponsable<FavoriteProductList.Response> {
            override fun onResponse(response: FavoriteProductList.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                    response.data.favorites.forEach {
//                        it.prdtId = "A391331"
                        Trace.debug(">> favorite productId = ${it.prdtId}")
                        it.bOrigin = true
                        it.bFavorite = true
                        mFavorites.add(it)
                    }
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }

    private fun requestAddFavorite() {
        mFavorites.forEach {
            Trace.debug(">> requestAddFavorite() bOrigin = ${it.bOrigin} bFavorite = ${it.bFavorite} pid = ${it.prdtId}")
            if (!it.bOrigin && it.bFavorite) {
                val addProtocol: FavoriteProductAddProtocol = ProtocolFactory.create(FavoriteProductAddProtocol::class.java)
                addProtocol.setJsonRequestBody(FavoriteProductList.AddRequest(it.patnrId, it.prdtId))

                addProtocol.setHttpResponsable(object : HttpResponsable<BaseResponse> {
                    override fun onResponse(response: BaseResponse) {
                        Trace.debug(">> requestData() onResponse() : $response")

                        if (!response.isSuccess()) {
                            Toast.makeText(ApplicationProxy.getContext(), "Favorite add failed.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(nError: Int, strMsg: String) {
                        Trace.debug(">> requestData() onFailure($nError) : $strMsg")
                    }
                })

                NetworkManager.getInstance().asyncRequest(addProtocol)
            }
        }
    }

    private fun requestDeleteFavorite() {
        mFavorites.forEach {
            Trace.debug(">> requestDeleteFavorite() bOrigin = ${it.bOrigin} bFavorite = ${it.bFavorite} pid = ${it.prdtId}")
            if (it.bOrigin && !it.bFavorite) {
                val deleteProtocol: FavoriteProductDeleteProtocol = ProtocolFactory.create(FavoriteProductDeleteProtocol::class.java)
                deleteProtocol.setJsonRequestBody(FavoriteProductList.DeleteRequest(it.patnrId, it.prdtId))

                deleteProtocol.setHttpResponsable(object : HttpResponsable<BaseResponse> {
                    override fun onResponse(response: BaseResponse) {
                        Trace.debug(">> requestData() onResponse() : $response")

                        if (!response.isSuccess()) {
                            Toast.makeText(ApplicationProxy.getContext(), "Favorite delete failed.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(nError: Int, strMsg: String) {
                        Trace.debug(">> requestData() onFailure($nError) : $strMsg")
                    }
                })

                NetworkManager.getInstance().asyncRequest(deleteProtocol)
            }
        }
    }
}