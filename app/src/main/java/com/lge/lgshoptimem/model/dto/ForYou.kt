package com.lge.lgshoptimem.model.dto

class ForYou()
{
    data class Response(val data: Data): BaseResponse()
    {
        override fun toString(): String {
            return super.toString() + data.toString()
        }

        data class Data(
            val alerts: ArrayList<Alert>,
            val favorites: ArrayList<Favorite>,
            val upcomAlamUseFlag: String,
            val alertShows: ArrayList<UpcomingShow>
        )
    }

    data class Alert(
            val keywd: String,
            val showList: ArrayList<KeywordShow>,
    ): Header()
    {
        override fun getSubTitle() = if (keywd.isEmpty()) "" else "#$keywd"
    }

    data class KeywordShow(
        // Show
        val patnrId: String,
        val patncNm: String,
        val patncLogoPath: String,
        val showId: String,
        val chanId: String,
        val showNm: String,
        val showDesc: String,
        val showUrl: String,
        val strtDt: String,
        val endDt: String,
        val showTimesec: String,
        val liveFlag: String,
        val timezone: String,
        val hstNm: String,
        val thumbnailUrl: String,
        val dfltThumbnailImgPath: String,
        val catNm: String,
        val catCd: String,
        val chanExpsTpCd: String,
        val orderPhnNo: String,

        val lgCatCd: String,
        val lgCatNm: String,
        val file_path: String,
        val keywd: String
    )

    data class Favorite(
        val dataTp: String,
        val showId: String,
        val showNm: String,
        val thumbnailUrl: String,
        val price: String,
        val soldOutYn: String,
        val chgDt: String
    ): Product()
    {
        override fun isSoldOut() = (soldOutYn == "Y")

        override fun getShopTimePrice() = price
    }

    data class UpcomingShow(
        val showId: String,
        val showNm: String,
        val patnrId: String,
        val patncNm: String,
        val patncLogoPath: String,
        val hstNm: String,
        val thumbnailUrl: String,
        val strtDt: String,
        val endDt: String,
        val regDt: String,
        val alamDispFlag: String
    )
}
