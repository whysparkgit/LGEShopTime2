package com.lge.lgshoptimem.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityForYouSettingBinding
import com.lge.lgshoptimem.databinding.CompItemKeywordBinding
import com.lge.lgshoptimem.ui.common.ActionBar

class ForYouSettingActivity : AppCompatActivity() , ActionBar.onActionBarListener {
    private lateinit var mBinding: ActivityForYouSettingBinding
    private val keywordListAdapter = KeywordListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_for_you_setting)

        val actionBar = ActionBar(this)
        actionBar.title = getString(R.string.for_you_setting)
        actionBar.setButton(ActionBar.ACTION_BACK, ActionBar.ACTION_NONE)
        mBinding.actionbar = actionBar

        mBinding.fysRvRecyclerview.apply {
            layoutManager = GridLayoutManager(applicationContext,3)
            adapter = keywordListAdapter
        }
    }

    class KeywordListAdapter : RecyclerView.Adapter<KeywordListAdapter.KeywordViewHolder>() {
        //set Item of swipe recyclerView
        private val items: MutableList<String> = mutableListOf<String>().apply {
            for (i in 0..10)
                add("2020 : $i sample keyword")
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordViewHolder = KeywordViewHolder(
                CompItemKeywordBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
        )

        override fun onBindViewHolder(holder: KeywordViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class KeywordViewHolder(private val binding: CompItemKeywordBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(keyword: String) {
                binding.keyword = keyword

            }
        }
    }

    override fun onLeft() {
        onBackPressed()
    }

    override fun onRight() {
        //nothing
    }

    fun onBtnClick(view : View) {
        when (view.id) {
            R.id.fys_btn_cancel -> {
                onBackPressed()
            }
            R.id.fys_btn_save -> {
                Toast.makeText(this,"save_btn_clicked",Toast.LENGTH_SHORT).show()
            }
        }
    }

}