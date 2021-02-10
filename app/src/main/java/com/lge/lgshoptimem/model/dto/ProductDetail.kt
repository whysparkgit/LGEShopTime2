package com.lge.lgshoptimem.model.dto

class ProductDetail
{
    data class Response(val data: Data): BaseResponse()
    {
        override fun toString(): String {
            return super.toString() + data.toString()
        }

        data class Data(
            val product: ArrayList<ProductInfo>,
            val youmaylike: ArrayList<YouMayLike>
        )
    }
}
