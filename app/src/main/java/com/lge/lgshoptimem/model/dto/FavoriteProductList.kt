package com.lge.lgshoptimem.model.dto

class FavoriteProductList
{
    data class AddRequest(var patnrId: String, var prdtId: String, var lgCatCd: String? = null)

    data class DeleteRequest(var patnrId: String, var prdtId: String)

    data class Response(val data: Data): BaseResponse()
    {
        override fun toString(): String {
            return super.toString() + data.toString()
        }

        data class Data(
            val favorites: ArrayList<Favorite>
        )
    }
}
