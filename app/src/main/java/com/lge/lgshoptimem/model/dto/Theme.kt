package com.lge.lgshoptimem.model.dto

data class Theme(
    // curation
        val curationId: String,
        val patnrId: String,
        val patncNm: String,
        val patncLogoPath: String,
        val curationNm: String,
        val curationTpCd: String,
        val tvCategoryNm: String,
        val curationKeywd: String,
        val dpPositionCd: String,
        val crawlFlag: String,
        val crawlLgTag: String,
        val expsOrd: String,
        val tmpltCd: String,
        val stusCd: String,
        val expsStrtDt: String,
        val expsEndDt: String,
        val smsBtnExpsFlag: String,
        val freeShippingFlag: String,
        val todaySpclFlag: String,
        val bgImgNo: String,
        val showId: String,
        val showNm: String,
        val showUrl: String,
        val thumbnailUrl: String,
        val hstNm: String,
        val showTimesec: String,
        val showSubtitlUrl: String,
        val appId: String,
        val orderPhnNo: String,
        val productInfos: ArrayList<ProductInfo>,

        val bgImgPath: ArrayList<String>,
        val showEffectiveFlag: String,
        val dfltThumbnailImgPath: String,
        val hotelCheckIn: String,
        val hotelCheckOut: String,
        val hotelIdList: ArrayList<String>,
        val evntId: String,
        val eventInfo: String
)