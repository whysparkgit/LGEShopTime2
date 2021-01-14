package com.lge.lgshoptimem.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.core.sys.Const
import com.lge.lgshoptimem.model.dto.AppVersionList

class AppVersionProtocol : AbstractHttpProtocol<AppVersionList>()
{
    val PATH = "device/app.version"

    override fun getUrl(): String = getDomain() + Const.NETWORK.DEV_SERVER.PATH + PATH
}