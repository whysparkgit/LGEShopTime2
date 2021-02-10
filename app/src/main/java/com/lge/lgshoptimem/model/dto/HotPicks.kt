package com.lge.lgshoptimem.model.dto

class HotPicks
{
    data class Response(val data: Data): BaseResponse()
    {
        override fun toString(): String {
            return super.toString() + data.toString()
        }

        data class Data(
                val themeIndexExpsFlag: String,
                val themeInfos: ArrayList<Theme>,
                val adInfo: ArrayList<AdInfo>,
                val travelInfos: Travel,
                val curations: ArrayList<Curation>
        )
    }

    data class Travel(
        val amenities: ArrayList<Amenity>
    )

    data class Amenity(
        val patnrId: String,
        val imgInfos: ArrayList<AmenityInfo>
    )

    data class AmenityInfo(
        val amntId: String,
        val lgAmntNm: String,
        val lgAmntImgUrl: String,
        val lgAmntId: String
    )
}
