package com.lge.lgshoptimem.ui.home

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lge.core.app.ApplicationProxy
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.model.dto.*
import com.lge.lgshoptimem.model.http.ScheduleProtocol
import com.lge.lgshoptimem.model.http.UpcomingAlarmSetProtocol

class ScheduleViewModel : ViewModel() {
    val mldSchedule: MutableLiveData<Schedule.Response.Data> = MutableLiveData()

    /** Custom Component Data Query */
    fun requestData(partnerId: String, strDate: String) {
        val protocol: ScheduleProtocol = ProtocolFactory.create(ScheduleProtocol::class.java)
        protocol.setPartnerId(partnerId)
        protocol.setDate(strDate)

        protocol.setHttpResponsable(object : HttpResponsable<Schedule.Response> {
            override fun onResponse(response: Schedule.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                    mldSchedule.value = response.data
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }

//    fun requestUpcomingAlarm(request: UpcomingAlarm.Request) {
//        val setProtocol: UpcomingAlarmSetProtocol = ProtocolFactory.create(UpcomingAlarmSetProtocol::class.java)
//        setProtocol.setJsonRequestBody(request)
//
//        setProtocol.setHttpResponsable(object : HttpResponsable<BaseResponse> {
//            override fun onResponse(response: BaseResponse) {
//                Trace.debug(">> requestData() onResponse() : $response")
//
//                if (!response.isSuccess()) {
//                    Toast.makeText(ApplicationProxy.getContext(), "Favorite delete failed.", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(nError: Int, strMsg: String) {
//                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
//            }
//        })
//
//        NetworkManager.getInstance().asyncRequest(setProtocol)
//    }
}