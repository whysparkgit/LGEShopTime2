package com.lge.lgshoptimem.ui.more

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity.apply
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.getTag
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.setTag
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityAlarmBinding
import com.lge.lgshoptimem.databinding.CompItemAlarmBinding
import com.lge.lgshoptimem.ui.common.ActionBar
import com.lge.lgshoptimem.ui.common.SinglePopupDialog
import java.lang.Math.max
import java.lang.Math.min

class AlarmActivity : AppCompatActivity() , ActionBar.onActionBarListener{

    private lateinit var mBinding: ActivityAlarmBinding
    private val swipeAdapter = SwipeListAdapter()
    private val context = this

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_alarm)
        mBinding.activity = this


        //actionBar setting
        val actionBar = ActionBar(this)
        actionBar.title = getString(R.string.my_info)
        actionBar.setButton(ActionBar.ACTION_BACK, ActionBar.ACTION_NONE)
        mBinding.actionbar = actionBar

        val swipeHelperCallback = SwipeHelperCallback().apply {
            setClamp(dpToPx(context,68f))
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(mBinding.aaRvRecyclerview)

        mBinding.aaRvRecyclerview.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = swipeAdapter

            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }
        }
    }

    //Top ActionBar clickListener
    override fun onLeft() {
        onBackPressed()
    }

    override fun onRight() {
        //Do Nothing
    }

    //Item Click event
    //activity::onItemClick
    fun onItemClick(view : View) {
        when (view.id) {
            R.id.aa_btn_delete_all -> {
                SinglePopupDialog.Builder.create()
                        .setMessage(getString(R.string.delete_all))
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
        }
    }


    class SwipeHelperCallback : ItemTouchHelper.Callback() {

        private var currentPosition: Int? = null
        private var previousPosition: Int? = null
        private var currentDx = 0f
        private var clamp = 0f

        override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
        ): Int {
            // Drag와 Swipe 방향을 결정 Drag는 사용하지 않아 0, Swipe의 경우는 LEFT, RIGHT 모두 사용가능하도록 설정
            return makeMovementFlags(0, LEFT or RIGHT)
        }

        override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            currentDx = 0f
            previousPosition = viewHolder.adapterPosition
            getDefaultUIUtil().clearView(getView(viewHolder))
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            viewHolder?.let {
                currentPosition = viewHolder.adapterPosition
                getDefaultUIUtil().onSelected(getView(it))
            }
        }

        override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
        ) {

            if (actionState == ACTION_STATE_SWIPE) {
                val view = getView(viewHolder)
                val isClamped = getTag(viewHolder)
                val x =  clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)

                Log.d("asd", "currentDx : $x")
                currentDx = x

                getDefaultUIUtil().onDraw(
                        c,
                        recyclerView,
                        view,
                        x,
                        dY,
                        actionState,
                        isCurrentlyActive
                )
            }
        }
        private fun getView(viewHolder: RecyclerView.ViewHolder): View {
            return (viewHolder as SwipeListAdapter.SwipeViewHolder).itemView.findViewById(R.id.swipe_view)
        }

        //when you swipe this code prevent that waaaaay out wiped
        override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
            return defaultValue * 10
        }

        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            val isClamped = getTag(viewHolder)
            // 현재 View가 고정되어있지 않고 사용자가 -clamp 이상 swipe시 isClamped true로 변경 아닐시 false로 변경
            setTag(viewHolder, !isClamped && currentDx <= -clamp)
            return 2f
        }

        private fun clampViewPositionHorizontal(
                view: View,
                dX: Float,
                isClamped: Boolean,
                isCurrentlyActive: Boolean
        ) : Float {
            // View의 가로 길이의 절반까지만 swipe 되도록
            val min: Float = -view.width.toFloat()/2
            // RIGHT 방향으로 swipe 막기
            val max: Float = 0f

            val x = if (isClamped) {
                // View가 고정되었을 때 swipe되는 영역 제한
                if (isCurrentlyActive) dX - clamp else -clamp
            } else {
                dX
            }

            return min.coerceAtLeast(x).coerceAtMost(max)
        }

        private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
            // isClamped를 view의 tag로 관리
            viewHolder.itemView.tag = isClamped
        }

        private fun getTag(viewHolder: RecyclerView.ViewHolder) : Boolean {
            // isClamped를 view의 tag로 관리
            return viewHolder.itemView.tag as? Boolean ?: false
        }

        fun setClamp(clamp: Float) {
            this.clamp = clamp
        }

        // 다른 View가 swipe 되거나 터치되면 고정 해제
        fun removePreviousClamp(recyclerView: RecyclerView) {
            if (currentPosition == previousPosition)
                return
            previousPosition?.let {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
                getView(viewHolder).translationX = 0f
                setTag(viewHolder, false)
                previousPosition = null
            }
        }

    }

    class SwipeListAdapter : RecyclerView.Adapter<SwipeListAdapter.SwipeViewHolder>() {
        //set Item of swipe recyclerView
        private val items: MutableList<String> = mutableListOf<String>().apply {
            for (i in 0..10)
                add("2020 : $i sample date")
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwipeViewHolder = SwipeViewHolder(
                CompItemAlarmBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
        )

        override fun onBindViewHolder(holder: SwipeViewHolder, position: Int) {
            holder.bind(items[position])

        }

        override fun getItemCount(): Int = items.size

        inner class SwipeViewHolder(private val binding: CompItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(date: String) {
                binding.date = date
                // delete list item
                binding.ciaIvDelete.setOnClickListener {
                    Snackbar.make(it, "delete button of $date click $adapterPosition", Snackbar.LENGTH_SHORT).show()

                    items.removeAt(adapterPosition)
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

}