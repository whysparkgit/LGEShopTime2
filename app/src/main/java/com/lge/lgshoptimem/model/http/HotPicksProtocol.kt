package com.lge.lgshoptimem.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.lgshoptimem.model.dto.HotPicks

class HotPicksProtocol : AbstractHttpProtocol<HotPicks.Response>()
{
    val PATH = "main/hotpicks.lge"

    override fun getUrl() = getDomain() + getPath() + PATH

    override fun getRequestHeaderMap(): Map<String, String> {
        return super.getRequestHeaderMap().run {
            CommonHeader.getInstance().getHeaders()
        }
    }
}