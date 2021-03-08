package com.lge.lgshoptimem.model.dto

import androidx.room.*

//@Entity(indices = arrayOf(Index(value = ["lgCatCd"], unique = true)))
//@Entity(primaryKeys = arrayOf("catId", "lgCatCd"))
@Entity
data class CategoryInfo(
//    @PrimaryKey(autoGenerate = true)
//    var catId: Int = 0,
    @PrimaryKey
    var lgCatCd: String = "",
    var bFavorite: Boolean = false,
    @Ignore
    var catNm: String = "",
    @Ignore
    var expsOrd: String = "",
    @Ignore
    var catTpCd: String = "",
    @Ignore
    var categoryItems: ArrayList<Category>? = null
)
{
    data class Category(
        val patnrId: String,
        val patncNm: String,
        val patncLogoPath: String,
        val showId: String,
        val chanId: String,
        val showNm: String,
        val showDesc: String,
        val showUrl: String,
        val showTimesec: String,
        val liveFlag: String,
        val hstNm: String,
        val thumbnailUrl: String,
        val dfltThumbnailImgPath: String,
        val imgUrl: String,
        val favorYn: String,
        val banrId: String,
        val patncCatCd: String,
        val patncCatNm: String
    )
}
