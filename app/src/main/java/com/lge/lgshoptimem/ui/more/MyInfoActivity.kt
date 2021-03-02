package com.lge.lgshoptimem.ui.more

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.BaseObservable
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.lge.core.sys.Trace.Companion.debug
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityMyInfoBinding
import com.lge.lgshoptimem.ui.common.ActionBar
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.ui.common.SinglePopupDialog

class MyInfoActivity : AppCompatActivity() , ActionBar.onActionBarListener {

    private lateinit var mBinding: ActivityMyInfoBinding

    var phoneNum:String = ""
    var zipCode = ObservableField<String>("000000")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_info)
        mBinding.activity = this

        //actionBar setting
        val actionBar = ActionBar(this)
        actionBar.title = getString(R.string.my_info)
        actionBar.setButton(ActionBar.ACTION_BACK, ActionBar.ACTION_NONE)
        mBinding.actionbar = actionBar

        mBinding.buffer = zipCode

        //permission required
        getPhoneNumber()
    }

    fun onItemClick(view : View) {

        val intent = Intent()

        when(view.id) {
            R.id.ami_btn_edit -> {
                intent.setClass(this,EditZipCodeActivity::class.java)
                intent.putExtra("zipCode",zipCode.get())
                startActivityForResult(intent, Companion.REQUEST_ZIP_CODE)
            }

            R.id.ami_tv_log_out -> {

            }

            R.id.ami_tv_delete_my_info -> {
                //Dialog 띄우기.
                SinglePopupDialog.Builder.create()
                        .setMessage(getString(R.string.delete_my_info_guide))
                        .setPositiveButton(getString(R.string.cancel_eng), DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })
                        .setNegativeButton(getString(R.string.delete_eng), DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                            //delete my info


                        })
                        .setCancelable(false)
                        .show()

            }

            R.id.ami_btn_delete -> {
                SinglePopupDialog.Builder.create()
                        .setMessage(getString(R.string.delete_info_guide))
                        .setPositiveButton(getString(R.string.cancel_eng), DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })
                        .setNegativeButton(getString(R.string.delete_eng), DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                            //delete device
                        })
                        .setCancelable(false)
                        .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                //Zip Code return
                Companion.REQUEST_ZIP_CODE -> {
                    Trace.debug("data!!.getStringExtra : ${data!!.getStringExtra("zipCode")}")
                    zipCode.set(data.getStringExtra("zipCode"))
                    Trace.debug("zipCode : $zipCode")
                }
            }
        }
    }

    @SuppressLint("HardwareIds")
    fun getPhoneNumber() {
        val telManager: TelephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            return
        } else {
            phoneNum = telManager.line1Number
        }
        phoneNum = telManager.line1Number
        if (phoneNum.startsWith("+82")) {
            phoneNum = phoneNum.replace("+82", "0")
        }
    }

    override fun onLeft() {
        onBackPressed()
    }

    override fun onRight() {
        //nothing in here
    }

    companion object {
        const val REQUEST_ZIP_CODE = 200
    }
}