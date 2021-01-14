package com.lge.lgshoptimem.model.dto

import com.lge.core.net.HttpConst

open class BaseResponse()
{
    val retCode: Int = 0
    val retMsg: String = ""

    fun isSuccess(): Boolean {
        return (retCode == HttpConst.HTTP_RESPONSE_SUCCESS)
    }

    override fun toString(): String {
        var strBuf: String = "\n"

        strBuf += "BaseResponse.retCode = $retCode\n"
        strBuf += "BaseResponse.retMsg = $retMsg\n"

        return strBuf
    }
}