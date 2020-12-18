package com.lge.shoptime.model.dto

data class AppVersionList(val resultList: List<AppVerInfo>): BaseResponse()
{
    data class AppVerInfo(
            val appFileId: String,
            val appNm: String,
            val appPkgNm: String,
            val appVrsnCd: Int,
            val appVrsnNm: String,
            val forceUptYn: String
    )
}