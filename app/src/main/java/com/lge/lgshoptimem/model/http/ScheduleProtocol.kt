package com.lge.lgshoptimem.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.core.sys.Const
import com.lge.lgshoptimem.model.dto.CurationList
import com.lge.lgshoptimem.model.dto.ProductDetail
import com.lge.lgshoptimem.model.dto.Schedule
import com.lge.lgshoptimem.model.dto.WatchNow

class ScheduleProtocol : AbstractHttpProtocol<Schedule.Response>()
{
    val PATH = "main/live/upcoming/sub.lge"

    override fun getUrl() = getDomain() + getPath() + PATH + getQueries()

    override fun getRequestHeaderMap(): Map<String, String> {
        return super.getRequestHeaderMap().run {
            CommonHeader.getInstance().getHeaders()
        }
    }

    fun setPartnerId(strId: String) = addQuery("patnrIdList", strId)

    fun setDate(strDate: String) = addQuery("strtDt", strDate)
}