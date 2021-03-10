package com.lge.lgshoptimem.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.core.net.HttpConst
import com.lge.lgshoptimem.model.dto.BaseResponse
import com.lge.lgshoptimem.model.dto.CategoryList
import com.lge.lgshoptimem.model.dto.FavoriteKeywordList
import com.lge.lgshoptimem.model.dto.KeywordList

class UpcomingAlarmSetProtocol : AbstractHttpProtocol<BaseResponse>()
{
    val PATH = "main/live/upcoming/alarm.lge"

    override fun getUrl() = getDomain() + getPath() + PATH

    override fun getRequestHeaderMap(): Map<String, String> {
        return super.getRequestHeaderMap().run {
            CommonHeader.getInstance().getHeaders()
        }
    }

    override fun getMethod() = HttpConst.HTTP_POST
}