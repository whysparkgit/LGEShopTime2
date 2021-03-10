package com.lge.lgshoptimem.model.dto

class FavoriteKeywordList
{
    data class AddRequest(var keywordList: ArrayList<String>)

    data class DeleteRequest(var Keywd: String)

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
