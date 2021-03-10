package com.lge.lgshoptimem.model.dto

class UpcomingAlarm
{
    data class Request(var patnrId: String,
                       var showId: String,
                       var strtDt: String,
                       var endDt: String,
                       var alamDispFlag: String)
}
