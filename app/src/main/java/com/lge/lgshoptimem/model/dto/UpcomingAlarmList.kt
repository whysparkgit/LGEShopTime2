package com.lge.lgshoptimem.model.dto

class UpcomingAlarmList
{
    data class Response(val data: Data): BaseResponse()
    {
        override fun toString(): String {
            return super.toString() + data.toString()
        }

        data class Data(
            val upcomAlamUseFlag: String,
            val alertShows: ArrayList<Show>
        )
    }
}
