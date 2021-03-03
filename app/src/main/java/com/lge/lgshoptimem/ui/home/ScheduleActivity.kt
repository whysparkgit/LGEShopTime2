package com.lge.lgshoptimem.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityScheduleBinding
import com.lge.lgshoptimem.databinding.CompItemScheduleBinding
import com.lge.lgshoptimem.ui.common.ActionBar

class ScheduleActivity : AppCompatActivity(), ActionBar.onActionBarListener {

    private lateinit var mBinding: ActivityScheduleBinding
//    private val scheduleAdapter: ScheduleAdapter = ScheduleAdapter()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_schedule)
        mBinding.activity = this

        //actionBar setting
        val actionBar = ActionBar(this)
        actionBar.title = getString(R.string.schedule)
        actionBar.setButton(ActionBar.ACTION_BACK, ActionBar.ACTION_NONE)
        mBinding.actionbar = actionBar

//        mBinding.ascRvDate.apply {
//            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
//            adapter = scheduleAdapter
//        }
    }




//    class ScheduleItem(dataString : String, dataNum : Int) {
//        var dataString: String? = dataString
//        var dataNum: Int? = dataNum
//    }
//
//    class ScheduleAdapter() : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {
//        //set Item of swipe recyclerView
//        private val items: MutableList<ScheduleItem> = mutableListOf<ScheduleItem>().apply {
//            for (i in 0..10)
//                add(ScheduleItem("MON", i))
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder = ScheduleViewHolder(
//                CompItemScheduleBinding.inflate(
//                        LayoutInflater.from(parent.context),
//                        parent,
//                        false
//                )
//        )
//
//        override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
//            holder.bind(items[position])
//
//        }
//
//        override fun getItemCount(): Int = items.size
//
//        inner class ScheduleViewHolder(private val binding: CompItemScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
//            fun bind(scheduleItem: ScheduleItem) {
//                binding.dateString = scheduleItem.dataString
//                binding.dateNum = scheduleItem.dataNum.toString()
//
//                binding.ciaIvDelete.setOnClickListener {
//                    Snackbar.make(it, "delete button of $date click $adapterPosition", Snackbar.LENGTH_SHORT).show()
//
//                    items.removeAt(adapterPosition)
//                    notifyDataSetChanged()
//                }
//            }

//        }
//    }


    //Top ActionBar clickListener
    override fun onLeft() {
        onBackPressed()
    }

    override fun onRight() {
        //Do Nothing
    }
}