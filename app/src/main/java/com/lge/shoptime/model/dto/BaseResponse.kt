package com.lge.shoptime.model.dto

import com.lge.core.net.HttpConst

open class BaseResponse()
{
    val retCode: Int = 0
    val retMsg: String = ""

    fun isSuccess(): Boolean {
        return (retCode == HttpConst.HTTP_RESPONSE_SUCCESS)
    }
}