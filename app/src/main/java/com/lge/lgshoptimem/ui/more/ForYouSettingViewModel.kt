package com.lge.lgshoptimem.ui.more

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lge.core.app.ApplicationProxy
import com.lge.core.db.DatabaseManager
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.model.dto.*
import com.lge.lgshoptimem.model.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForYouSettingViewModel : ViewModel() {
    val mldCategoryList: MutableLiveData<CategoryList.Response.Data> = MutableLiveData()
    val mCategoryDao = DatabaseManager.getInstance().getDatabase().getCategoryDao()

    val mldKeywordList: MutableLiveData<KeywordList.Response.Data> = MutableLiveData()
    var mKeywordList: KeywordList.Response.Data? = null

    /** Custom Component Data Query */
    fun requestCategory() {
        val protocol: CategoryListProtocol = ProtocolFactory.create(CategoryListProtocol::class.java)
        var localItems: List<CategoryInfo>? = null

        CoroutineScope(Dispatchers.IO).launch {
            localItems = mCategoryDao.selectAll()
        }

        protocol.setHttpResponsable(object : HttpResponsable<CategoryList.Response> {
            override fun onResponse(response: CategoryList.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess() && !response.data.categoryInfos.isNullOrEmpty()) {
                    Trace.debug(">> items.size = ${localItems?.size}")

                    if (!localItems.isNullOrEmpty()) {
                        for (remote: CategoryInfo in response.data.categoryInfos) {
                            for (local: CategoryInfo in localItems!!) {
                                if (remote.lgCatCd == local.lgCatCd) {
                                    remote.bFavorite = local.bFavorite
                                    Trace.debug(">> remote.catNm = ${remote.catNm} bFavorite = ${remote.bFavorite}")
                                    break
                                }
                            }
                        }
                    }

                    mldCategoryList.value = response.data
                } else {
                    Toast.makeText(ApplicationProxy.getContext(), "Data not exist.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }

    fun saveFavoriteCategory() {
        val favorites: ArrayList<CategoryInfo> = arrayListOf()

        mldCategoryList.value?.categoryInfos?.forEach {
            if (it.bFavorite) favorites.add(it)
            Trace.debug(">> name = ${it.catNm} like = ${it.bFavorite}")
        }

        CoroutineScope(Dispatchers.IO).launch {
            mCategoryDao.deleteAll()

            if (favorites.isNotEmpty()) {
                mCategoryDao.insertAll(favorites)
            }
        }
    }

    fun requestKeyword() {
        val protocol: KeywordListProtocol = ProtocolFactory.create(KeywordListProtocol::class.java)

        protocol.setHttpResponsable(object : HttpResponsable<KeywordList.Response> {
            override fun onResponse(response: KeywordList.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
//                    mldKeywordList.value = response.data
                    mKeywordList = response.data

                    if (mKeywordList != null && !mKeywordList!!.keywords.isNullOrEmpty()) requestFavoriteKeyword()
                } else {
                    Toast.makeText(ApplicationProxy.getContext(), "Data not exist.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }

    fun requestFavoriteKeyword() {
        val protocol: FavoriteKeywordListProtocol = ProtocolFactory.create(FavoriteKeywordListProtocol::class.java)

        protocol.setHttpResponsable(object : HttpResponsable<FavoriteKeywordList.Response> {
            override fun onResponse(response: FavoriteKeywordList.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                    if (!response.data.alerts.isNullOrEmpty()) {
                        for (original: KeywordList.Keyword in mKeywordList!!.keywords) {
                            for (favorite: KeywordAlert in response.data.alerts) {
                                if (original.keywd == favorite.keywd) {
                                    original.bOrigin = true
                                    original.bFavorite = true
                                }
                            }
                        }

                        mldKeywordList.value = mKeywordList
                        return
                    }
                }

                Toast.makeText(ApplicationProxy.getContext(), "Data not exist.", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }

    fun saveFavoriteKeyword() {
        var favoiteList: ArrayList<String> = arrayListOf()

        for (keyword: KeywordList.Keyword in mldKeywordList.value!!.keywords) {
            if (keyword.bOrigin && !keyword.bFavorite) {
                favoiteList.add(keyword.keywd)
            }
        }

        if (favoiteList.size > 0) {
            val protocol: DeleteFavoriteKeywordProtocol = ProtocolFactory.create(DeleteFavoriteKeywordProtocol::class.java)
            protocol.setRequestBody(favoiteList)

            protocol.setHttpResponsable(object : HttpResponsable<BaseResponse> {
                override fun onResponse(response: BaseResponse) {
                    Trace.debug(">> requestData() onResponse() : $response")
                }

                override fun onFailure(nError: Int, strMsg: String) {
                    Trace.debug(">> requestData() onFailure($nError) : $strMsg")
                }
            })

            NetworkManager.getInstance().asyncRequest(protocol)
        }

        favoiteList.clear()

        for (keyword: KeywordList.Keyword in mldKeywordList.value!!.keywords) {
            if (keyword.bFavorite) {
                favoiteList.add(keyword.keywd)
            }
        }

        if (favoiteList.size > 0) {
            val protocol: AddFavoriteKeywordProtocol = ProtocolFactory.create(AddFavoriteKeywordProtocol::class.java)
            protocol.setRequestBody(favoiteList)

            protocol.setHttpResponsable(object : HttpResponsable<BaseResponse> {
                override fun onResponse(response: BaseResponse) {
                    Trace.debug(">> requestData() onResponse() : $response")
                }

                override fun onFailure(nError: Int, strMsg: String) {
                    Trace.debug(">> requestData() onFailure($nError) : $strMsg")
                }
            })

            NetworkManager.getInstance().asyncRequest(protocol)
        }
    }
}