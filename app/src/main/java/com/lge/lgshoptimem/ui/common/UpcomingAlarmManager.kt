package com.lge.lgshoptimem.ui.common

import android.widget.Toast
import com.lge.core.app.ApplicationProxy
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.model.dto.*
import com.lge.lgshoptimem.model.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UpcomingAlarmManager
{
    companion object {
        private var mInstance: UpcomingAlarmManager? = null
        private const val SYNC_COUNT_MAX = 1

        @JvmStatic
        fun getInstance(): UpcomingAlarmManager = mInstance?:
        synchronized(this) {
            mInstance?: UpcomingAlarmManager().also {
                mInstance = it
            }
        }
    }

    var mUpcomings: ArrayList<Show> = arrayListOf()
    var mnSyncCount = 0

    init {
        requestData()
    }

    fun isAlarmChecked(showId: String): Boolean {
        var bRet = false

        mUpcomings.forEach {
            if (it.showId == showId) bRet = it.bAlarm
        }

        Trace.debug("++ isAlarmChecked() showId = $showId checked = $bRet")

        return bRet
    }

    fun addAlarm(show: Show) {
        Trace.debug("++ addAlarm() showId = ${show.showId} mnSyncCount = $mnSyncCount")

        mnSyncCount++

        mUpcomings.forEach {
            if (it.showId == show.showId) {
                it.bAlarm = true
                if (mnSyncCount >= SYNC_COUNT_MAX) syncData()
                return
            }
        }

        show.bAlarm = true
        mUpcomings.add(show)

        if (mnSyncCount >= SYNC_COUNT_MAX) syncData()
    }

    fun deleteAlarm(show: Show) {
        Trace.debug("++ deleteAlarm() showId = ${show.showId} mnSyncCount = $mnSyncCount")

        mnSyncCount++

        for (alarm: Show in mUpcomings) {
            if (alarm.showId == show.showId) {
                alarm.bAlarm = false
                break
            }
        }

        if (mnSyncCount >= SYNC_COUNT_MAX) syncData()
    }

    fun getAlarmUpcomings() = mUpcomings

    fun syncData() {
        Trace.debug("++ syncData() alarm sync")
        mnSyncCount = 0

        CoroutineScope(Dispatchers.IO).launch {
            requestSetAlarm()

            val iterator = mUpcomings.iterator()

            while (iterator.hasNext()) {
                val show = iterator.next()

                if (show.bAlarm) {
                    show.bOrigin = true
                } else {
                    iterator.remove()
                }
            }
        }
    }

    fun requestData() {
        val protocol: UpcomingAlarmListProtocol = ProtocolFactory.create(UpcomingAlarmListProtocol::class.java)

        protocol.setHttpResponsable(object : HttpResponsable<UpcomingAlarmList.Response> {
            override fun onResponse(response: UpcomingAlarmList.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                    response.data.alertShows.forEach {
//                        it.prdtId = "A391331"
                        Trace.debug(">> favorite productId = ${it.showId}")
                        it.bOrigin = true
                        it.bAlarm = true
                        mUpcomings.add(it)
                    }
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }

    fun requestSetAlarm() {
        mUpcomings.forEach {
            Trace.debug(">> requestSetAlarm() bOrigin = ${it.bOrigin} bAlarm = ${it.bAlarm} showId = ${it.showId}")
            if (it.bOrigin != it.bAlarm) {
                val request = UpcomingAlarm.Request(it.patnrId, it.showId, it.strtDt, it.endDt,if (it.bAlarm) "Y" else "N")
                val setProtocol: UpcomingAlarmSetProtocol = ProtocolFactory.create(UpcomingAlarmSetProtocol::class.java)
                setProtocol.setJsonRequestBody(request)

                setProtocol.setHttpResponsable(object : HttpResponsable<BaseResponse> {
                    override fun onResponse(response: BaseResponse) {
                        Trace.debug(">> requestData() onResponse() : $response")

                        if (!response.isSuccess()) {
                            Toast.makeText(ApplicationProxy.getContext(), "Favorite delete failed.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(nError: Int, strMsg: String) {
                        Trace.debug(">> requestData() onFailure($nError) : $strMsg")
                    }
                })

                NetworkManager.getInstance().asyncRequest(setProtocol)
            }
        }
    }
}