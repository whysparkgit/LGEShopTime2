package com.lge.lgshoptimem.model.dto

class CurationList
{
    data class Response(val data: Data): BaseResponse()
    {
        override fun toString(): String {
            return super.toString() + data.toString()
        }

        data class Data(
            val adInfo: ArrayList<AdInfo>,
            val curations: ArrayList<Curation>
        )
    }
}
