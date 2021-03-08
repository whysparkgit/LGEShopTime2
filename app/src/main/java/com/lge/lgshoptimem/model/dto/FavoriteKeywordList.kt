package com.lge.lgshoptimem.model.dto

class FavoriteKeywordList
{
    data class Response(val data: Data): BaseResponse()
    {
        override fun toString(): String {
            return super.toString() + data.toString()
        }

        data class Data(
                val alerts: ArrayList<KeywordAlert>,
        )
    }
}
