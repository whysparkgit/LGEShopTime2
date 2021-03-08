package com.lge.lgshoptimem.model.dto

data class KeywordAlert(
        val keywd: String,
        val showList: ArrayList<Show>,
): Header()
{
    override fun getSubTitle() = if (keywd.isEmpty()) "" else "#$keywd"
}