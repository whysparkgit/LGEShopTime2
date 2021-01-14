package com.lge.lgshoptimem.model.dto

import com.lge.core.net.HttpConst

class CurationList
{
    data class Response(
        val retCode: Int,
        val retMsg: String,
        val data: Data)
    {

        fun isSuccess(): Boolean {
            return (retCode == HttpConst.HTTP_RESPONSE_SUCCESS)
        }

        data class Data(
            val adInfo: ArrayList<AdInfo>,
            val curations: ArrayList<Curation>
        )
    }

    data class AdInfo(
        val curationId: String,
        val curationNm: String,
        val tmpltCd: String,
        val expsOrd: String
    )
}
