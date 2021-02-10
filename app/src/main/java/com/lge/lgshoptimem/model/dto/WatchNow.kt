package com.lge.lgshoptimem.model.dto

class WatchNow
{
    data class Response(val data: Data): BaseResponse()
    {
        override fun toString(): String {
            return super.toString() + data.toString()
        }

        data class Data(
                val period: String,
                val topInfos: ArrayList<TopInfo>,
                val adExpsFlag: String,
                val productInfos: ArrayList<ProductInfo>,
                val youmaylike: ArrayList<YouMayLike>,
                val upcomingItems: ArrayList<UpComing>,
                val lstChgDt: String,
                val adInfo: ArrayList<AdInfo>,
                val showInfos: ArrayList<Show>,
                val prdtChgYn: String,
                val disclaimer: String,
                val curations: ArrayList<Curation>
        )
    }

    data class TopInfo(
        val tvCategoryNm: String,
        val disclaimer: String,
        val productInfos: ArrayList<Product>
    ): Video()

    data class UpComing(
        val patncNm: String,
        val liveFlag: String,
        val strtDt: String,
        val timezone: String,
        val hstNm: String,
        val showDesc: String,
        val endDt: String,
        val showUrl: String,
        val alamDispFlag: String,
        val patncLogoPath: String,
        val showId: String,
        val patnrId: Int,
        val showTimesec: String,
        val showNm: String,
        val lgCatNm: String,
        val chanId: String,
        val lgCatCd: String,
        val thumbnailUrl: String
    )

    data class Show(
        val chanId: String,
        val showDesc: String,
        val strtDt: String,
        val endDt: String,
        val showTimesec: String,
        val liveFlag: String,
        val timezone: String,
        val hstNm: String,
        val dfltThumbnailImgPath: String,
        val chanExpsTpCd: String
    ): Video()

    open class Video {
        val showId: String = ""
        val showNm: String = ""
        val showUrl: String = ""
        val patnrId: String = ""
        val patncNm: String = ""
        val patncLogoPath: String = ""
        val catCd: String = ""
        val catNm: String = ""
        val orderPhnNo: String = ""
        val expsOrd: String = ""
        val thumbnailUrl: String = ""
        val showSubtitlUrl: String = ""
    }
}
