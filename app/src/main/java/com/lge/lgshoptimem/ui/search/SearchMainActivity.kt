package com.lge.lgshoptimem.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivitySearchMainBinding

class SearchMainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySearchMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_main)
        mBinding.listener = this

    }

    fun onClick(view: View) {
        val intent = Intent()
        when(view.id) {
            R.id.asm_ll_schedule -> {
                //schedule 이동.
                Toast.makeText(this,"schedule", Toast.LENGTH_SHORT).show()
            }
            R.id.asm_cl_search -> {
                //서칭
                intent.setClass(this,SearchActivity::class.java)
                startActivity(intent)
                Toast.makeText(this,"search",Toast.LENGTH_SHORT).show()
            }
        }
    }

}