package com.lge.lgshoptimem.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.core.sys.Const
import com.lge.lgshoptimem.model.dto.CurationList
import com.lge.lgshoptimem.model.dto.WatchNow

class WatchNowProtocol : AbstractHttpProtocol<WatchNow.Response>()
{
    val PATH = "main/watchnow.lge"

    override fun getUrl() = getDomain() + getPath() + PATH + getQueries()

    override fun getRequestHeaderMap(): Map<String, String> {
        return super.getRequestHeaderMap().run {
            CommonHeader.getInstance().getHeaders()
        }
    }

    fun setPartnerId(strId: String) = addQuery("patnrId", strId)

    fun setShowId(strId: String) = addQuery("showId", strId)

    fun setCategory(strCat: String) = addQuery("catCd", strCat)
}