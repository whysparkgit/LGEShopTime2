package com.lge.lgshoptimem.model.dto

class ForYou()
{
    data class Response(val data: Data): BaseResponse()
    {
        override fun toString(): String {
            return super.toString() + data.toString()
        }

        data class Data(
            val alerts: ArrayList<KeywordAlert>,
            val favorites: ArrayList<Favorite>,
            val upcomAlamUseFlag: String,
            val categoryAlertShow: ArrayList<CategoryAlert>,
            val alertShows: ArrayList<Show>
        )
    }

    data class CategoryAlert(
        val lgCatCd: String,
        val catNm: String,
        val expsOrd: String,
        val catTpCd: String,
        val categoryItems: ArrayList<Show>
    ): Header()
    {
        override fun getSubTitle() = catNm
    }

//    data class KeywordProduct(
//        // Show
//        val patnrId: String,
//        val patncNm: String,
//        val patncLogoPath: String,
//        val showId: String,
//        val chanId: String,
//        val showNm: String,
//        val showDesc: String,
//        val showUrl: String,
//        val strtDt: String,
//        val endDt: String,
//        val showTimesec: String,
//        val liveFlag: String,
//        val timezone: String,
//        val hstNm: String,
//        val thumbnailUrl: String,
//        val dfltThumbnailImgPath: String,
//        val catNm: String,
//        val catCd: String,
//        val chanExpsTpCd: String,
//        val orderPhnNo: String,
//
//        val lgCatCd: String,
//        val lgCatNm: String,
//        val file_path: String,
//        val keywd: String
//    ): Show()

//    data class UpcomingShow(
//        val showId: String,
//        val showNm: String,
//        val patnrId: String,
//        val patncNm: String,
//        val patncLogoPath: String,
//        val hstNm: String,
//        val thumbnailUrl: String,
//        val strtDt: String,
//        val endDt: String,
//        val regDt: String,
//        val alamDispFlag: String,
//        val lgCatCd: String,
//        val lgCatNm: String
//    ): Show()
}
