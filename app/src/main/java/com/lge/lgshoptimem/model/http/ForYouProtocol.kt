package com.lge.lgshoptimem.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.lgshoptimem.model.dto.ForYou

class ForYouProtocol : AbstractHttpProtocol<ForYou.Response>()
{
    val PATH = "main/foryou.lge"

    override fun getUrl() = getDomain() + getPath() + PATH

    override fun getRequestHeaderMap(): Map<String, String> {
        return super.getRequestHeaderMap().run {
            CommonHeader.getInstance().getHeaders()
        }
    }
}