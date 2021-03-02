package com.lge.lgshoptimem.ui.more

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityEditZipCodeBinding
import com.lge.lgshoptimem.ui.common.ActionBar
import com.lge.core.sys.Trace

class EditZipCodeActivity : AppCompatActivity() , ActionBar.onActionBarListener {

    private lateinit var mBinding: ActivityEditZipCodeBinding
    var zipCode = ObservableField<String>("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_zip_code)
        mBinding.activity = this


        setActionBar()

        val intent = intent
        intent.getStringExtra("zipCode")
        zipCode.set(intent.getStringExtra("zipCode"))

        zipCode.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                Trace.debug("sender.toString() = ${sender.toString()}" )
                Trace.debug("zipCode input  = ${zipCode.get()}")
            }
        })
        mBinding.buffer = zipCode
    }

    fun setActionBar() {
        var actionBar = ActionBar(this)
        actionBar.title = getString(R.string.edit_zip_code)
        actionBar.setButton(ActionBar.ACTION_BACK, ActionBar.ACTION_NONE)
        mBinding.actionbar = actionBar
    }

    //하단 저장, 취소 버튼
    fun onBtnClick(view : View) {
        when(view.id) {
            R.id.aez_btn_save -> {
                val intent = Intent()
                intent.putExtra("zipCode", zipCode.get())
                Trace.debug("zipCode Send : ${zipCode.get()}")
                setResult(RESULT_OK, intent)
                finish()
            }

            R.id.aez_btn_cancel -> {
                finish()
            }
        }
    }

    override fun onLeft() {
        finish()
    }

    override fun onRight() {
        //nothing
    }

}