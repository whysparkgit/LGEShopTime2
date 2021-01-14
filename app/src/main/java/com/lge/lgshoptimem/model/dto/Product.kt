package com.lge.lgshoptimem.model.dto

data class Product(
    val prdtId: String,
    val prdtNm: String,
    val prdtDesc: String,
    val price1: String,
    val price2: String,
    val price3: String,
    val price4: String,
    val price5: String,
    val rewd: String,
    val lgCatCd: String,
    val lgCatNm: String,
    val todaySpclFlag: String,
    val freeShippingFlag: String,
    val brndNm: String,
    val brndCatNm: String,
    val prdtTag: String,
    val imgUrl: String
)