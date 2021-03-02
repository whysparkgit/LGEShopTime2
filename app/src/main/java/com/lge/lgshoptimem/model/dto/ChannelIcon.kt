package com.lge.lgshoptimem.model.dto

open class ChannelIcon(
    var selected: Boolean,
    var resourceId: Int,
    var iconUrl: String)
{
    open fun getChannelIconUrl() = iconUrl
}

