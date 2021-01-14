package com.lge.lgshoptimem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.databinding.ActivityMainBinding
import com.lge.lgshoptimem.model.dto.Curation
import com.lge.lgshoptimem.model.dto.CurationList
import com.lge.lgshoptimem.model.http.MainCurationProtocol
import com.lge.lgshoptimem.ui.component.HeaderListComponent

class MainActivity : AppCompatActivity()
{
    private lateinit var mBinding: ActivityMainBinding
    private val mViewModel: HeaderListComponent.ComponentViewModel<Curation> by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.listener = this
        init()
    }

    fun init() {
        mBinding.compHilist.mnItemCountLimit = 7
        mBinding.compHilist.setTitle("PRODUCT LIST")

        mBinding.compThlist.mnItemCountLimit = 3
        mBinding.compThlist.setTitle("NEXT UPCOMINGS")

    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.btn_list1 -> {
                mBinding.compThlist.refresh()
            }

            R.id.btn_list2 -> {
                mBinding.compHilist.refresh()
            }
        }
    }

    /** Custom Component Data Query */
    fun HeaderListComponent.requestData() {
        val protocol: MainCurationProtocol = ProtocolFactory.create(MainCurationProtocol::class.java)

        protocol.setHttpResponsable(object : HttpResponsable<CurationList.Response> {
            override fun onResponse(response: CurationList.Response) {
                Trace.debug(">> requestData() onResponse() : $response")

                if (response.isSuccess()) {
                    mViewModel.mldDataList.value = response.data.curations
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestData() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }
}