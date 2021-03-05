package com.lge.lgshoptimem.ui.search

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity(){
    private lateinit var mBinding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        mBinding.listener = this

        supportFragmentManager.beginTransaction()
                .replace(R.id.ase_fl_container,KeywordFragment()).commit()

    }

    fun onClick(view: View) {
        when(view.id) {
            R.id.ase_iv_search -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.ase_fl_container,ResultFragment()).commit()
            }
            R.id.ase_iv_back -> {
                onBackPressed()
            }
        }
    }

}