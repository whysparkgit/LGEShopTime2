package com.lge.lgshoptimem.model.dto

class FavoriteProductList
{
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
