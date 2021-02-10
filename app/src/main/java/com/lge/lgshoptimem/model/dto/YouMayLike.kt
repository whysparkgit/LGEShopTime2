package com.lge.lgshoptimem.model.dto

data class YouMayLike(
    val patnrId: String,
    val patncLogoPath: String,
    val price: String,
    val Price1: String,
    val Price2: String,
    val Price3: String,
    val Price4: String,
    val Price5: String,
    val soldOutYn: String,
    val rewd: String,
): Product()
{
    override fun isSoldOut() = (soldOutYn == "Y")

    override fun getOriginPrice() = Price2

    override fun getShopTimePrice() = price

    override fun getDiscount() = Price5
}