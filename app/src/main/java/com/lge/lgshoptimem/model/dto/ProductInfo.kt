package com.lge.lgshoptimem.model.dto

data class ProductInfo(
    val prdtDesc: String,
    val price1: String,
    val price2: String,
    val price3: String,
    val price4: String,
    val price5: String,
    val rewd: String,
    val lgCatCd: String,
    val lgCatNm: String,
    val todaySpclFlag: String,
    val freeShippingFlag: String,
    val brndNm: String,
    val brndCatNm: String,
    val prdtTag: String,
    val installmentMonths: String,
    val offsetHour: String,
    val prdtMediaUrl: String,
    val prdtMediaNm: String,
    val thumbnailUrl: String,
    val prdtMediaSubtitlUrl: String,
    val imgUrls: ArrayList<String>,
    val catCd: String,
    val catNm: String,
    val patnrId: String,
    val patncNm: String,
    val showId: String,
    val favorYn: String,
    val expsPrdtNo: String,
    val patncLogoPath: String,
    val bestFlag: String,
    val soldoutFlag: String,
    val grPrdtFlag: String,
    val orderPhnNo: String,
    val disclaimer: String,
    val curationId: String,
    val curationNm: String
): Product()
{
    override fun getProductThumbnail(): String {
        if (imgUrls.isNullOrEmpty()) return imgUrl
        return imgUrls[0]
    }

    override fun isSoldOut(): Boolean {
        return (soldoutFlag == "Y")
    }
}