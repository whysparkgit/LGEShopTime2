package com.lge.lgshoptimem.model.dto

class CategoryList
{
    data class Response(val data: Data): BaseResponse()
    {
        override fun toString(): String {
            return super.toString() + data.toString()
        }

        data class Data(
            val tagInfos: ArrayList<Tag>,
            val categoryInfos: ArrayList<CategoryInfo>
        )
    }

    data class Tag(
            val lgTagCatCd: String,
            val expsOrd: String,
            val catNm: String,
            val iconImgPath: String,
            val lgCatCdList: ArrayList<String>,
            var bFavorite: Boolean = false,
    )
}
