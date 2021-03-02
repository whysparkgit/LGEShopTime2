package com.lge.lgshoptimem.ui.more

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityMoreBinding

class MoreActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_more)
        mBinding.listener = this

    }

    fun onItemClick(view : View) {

        val intent = Intent()
        when(view.id) {
            R.id.am_cl_my_info -> {
                intent.setClass(this,MyInfoActivity::class.java)
                startActivity(intent)
            }

            R.id.am_ll_coupon -> {
                intent.setClass(this,CouponActivity::class.java)
                startActivity(intent)
            }

            R.id.am_ll_alarm -> {
                intent.setClass(this,AlarmActivity::class.java)
                startActivity(intent)
            }

            R.id.am_ll_recently -> {
                Toast.makeText(this,view.id.toString(),Toast.LENGTH_SHORT).show()
            }

            R.id.am_ll_for_you -> {
                Toast.makeText(this,view.id.toString(),Toast.LENGTH_SHORT).show()
            }

            R.id.am_ll_favorites -> {
                Toast.makeText(this,view.id.toString(),Toast.LENGTH_SHORT).show()
            }

            R.id.am_ll_app_version -> {
                Toast.makeText(this,view.id.toString(),Toast.LENGTH_SHORT).show()
            }

            R.id.am_ll_support -> {
                Toast.makeText(this,view.id.toString(),Toast.LENGTH_SHORT).show()
            }

            R.id.am_ll_terms_service -> {
                Toast.makeText(this,view.id.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }

}