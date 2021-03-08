package com.lge.lgshoptimem.model.dto

data class Favorite(
        val dataTp: String,
        val showId: String,
        val showNm: String,
        val thumbnailUrl: String,
        val price: String,
        val soldOutYn: String,
        val chgDt: String,
        val dfltThumbnailImgPath: String
): Product()
{
    override fun isSoldOut() = (soldOutYn == "Y")

    override fun getShopTimePrice() = price
}