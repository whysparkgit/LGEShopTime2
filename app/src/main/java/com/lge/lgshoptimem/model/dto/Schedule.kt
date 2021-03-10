package com.lge.lgshoptimem.model.dto

class Schedule
{
    data class Response(val data: Data): BaseResponse()
    {
        override fun toString(): String {
            return super.toString() + data.toString()
        }

        data class Data(
            val partnerInfos: ArrayList<Video>,
            val upcomingItems: ArrayList<Show>
        )
    }
}
