package com.lge.lgshoptimem.ui.more

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.model.dto.Product

class CouponListAdapter(private val items: ArrayList<Product>) : RecyclerView.Adapter<CouponListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponListAdapter.ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
                .inflate(R.layout.comp_item_coupon, parent, false)

        return CouponListAdapter.ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: CouponListAdapter.ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener {
            it -> Toast.makeText(it.context, "Clicked : $position", Toast.LENGTH_SHORT).show()
        }

        holder.apply {
            bind(listener ,item)
            itemView.tag = item
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        fun bind(listener: View.OnClickListener, item: Product) {

        }
    }

}