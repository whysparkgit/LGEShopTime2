package com.lge.core.net

interface HttpRequestable
{
    fun getMethod(): Int

    fun getDomain(): String

    fun getUrl(): String

    fun getConnectTimeout(): Int

    fun getReadTimeout(): Int

    fun getRequestHeaderMap(): Map<String, String>

    fun getRequestQueryMap(): Map<String, String>

    fun getContentType(): String

    fun getRequestBody(): Any?

    fun getJsonRequestBody(): String?

    fun setResponseHeaderMap(responseHeaders: Map<String, List<String>>?)

    fun parse(responseBody: Any?)

    fun requestFailed(nError: Int, strMsg: String)

    fun hasOfflineJob(): Boolean

    fun processOffline()
}