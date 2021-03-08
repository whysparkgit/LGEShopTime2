package com.lge.lgshoptimem.model.dto

class KeywordList
{
    data class Response(val data: Data): BaseResponse()
    {
        override fun toString(): String {
            return super.toString() + data.toString()
        }

        data class Data(
                val keywords: ArrayList<Keyword>,
        )
    }

    data class Keyword(
            val expsOrd: String,
            val keywd: String,
            var bOrigin: Boolean = false,
            var bFavorite: Boolean = false,
    )
}
