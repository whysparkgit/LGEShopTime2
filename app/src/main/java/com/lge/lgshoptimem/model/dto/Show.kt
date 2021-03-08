package com.lge.lgshoptimem.model.dto

data class Show(
    val chanId: String,
    val showDesc: String,
    val strtDt: String,
    val endDt: String,
    val showTimesec: String,
    val timezone: String,
    val hstNm: String,
    val dfltThumbnailImgPath: String,
    val chanExpsTpCd: String,

    // Upcoming
    val alamDispFlag: String,
    val lgCatNm: String,
    val lgCatCd: String,

    // For You Keyword
    val file_path: String,
    val keywd: String,

    // For You Upcoming
    val regDt: String,
): Video()
