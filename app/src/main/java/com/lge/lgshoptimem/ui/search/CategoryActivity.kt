package com.lge.lgshoptimem.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivitySearchCategoryBinding


class CategoryActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySearchCategoryBinding
    private var isListClosed: Boolean = true

    var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_category)
        mBinding.listener = this

        val categoryListView = mBinding.scRvCategories
        val categoryItems = arrayOf("test1", "asd2", "qweqwe")
        title = categoryItems[0]

        val categoryAdapter = ListViewAdapter(this, categoryItems)
        categoryListView.adapter = categoryAdapter

        categoryListView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            val item = parent.getItemAtPosition(position) as String

            ShowList()
            title = item
            mBinding.invalidateAll()
            Toast.makeText(this, "$item clicked", Toast.LENGTH_SHORT).show()
        }
    }


    class ListViewAdapter(val context: Context, private val items: Array<String>) : BaseAdapter() {
        private val mInflater: LayoutInflater = LayoutInflater.from(context)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            Trace.debug("+++ getView called >>>>")

            val view: View
            val vh: ItemRowHolder

            if (convertView == null) {
                view = mInflater.inflate(R.layout.comp_item_category_list, parent, false)
                vh = ItemRowHolder(view)
                view?.tag = vh
            } else {
                view = convertView
                vh = view.tag as ItemRowHolder
            }

            vh.label.text = items.get(position)

            Trace.debug("+++ getView called >>>>")
            return view
        }

        private class ItemRowHolder(row: View?) {
            val label: TextView = row?.findViewById(R.id.cic_tv_category) as TextView
        }

        override fun getCount(): Int = items.size

        override fun getItem(position: Int): String = items[position]

        override fun getItemId(position: Int): Long = position.toLong()
    }

    fun ShowList() {
        if (isListClosed) {
            mBinding.scIbFold.isSelected = true
            mBinding.scVDim.visibility = View.VISIBLE
            mBinding.scRvCategories.visibility = View.VISIBLE
            isListClosed = false
        } else {
            mBinding.scIbFold.isSelected = false
            mBinding.scVDim.visibility = View.GONE
            mBinding.scRvCategories.visibility = View.GONE
            isListClosed = true
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.sc_ib_filter -> {
                val filterDialog = FilterDialog()
                filterDialog.show(supportFragmentManager, "filter")
            }
            R.id.sc_iv_back -> {
                finish()
                Toast.makeText(this, "back button clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.sc_ib_fold -> {
                ShowList()
            }

            R.id.sc_inc_top -> {
                ShowList()
            }

        }
    }
}