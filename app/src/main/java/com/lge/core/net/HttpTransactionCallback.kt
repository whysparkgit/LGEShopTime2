package com.lge.core.net

interface HttpTransactionCallback
{
    fun transactionBegin()

    fun transactionEnd()
}