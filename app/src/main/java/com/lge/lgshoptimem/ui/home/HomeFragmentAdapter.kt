package com.lge.lgshoptimem.ui.home

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeFragmentAdapter(var fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    private val fragments = SparseArray<Fragment>()

    override fun createFragment(position: Int): Fragment {
        lateinit var fragment: Fragment

        when (position) {
            0 -> {
                fragment = ForYouFragment.newInstance()
                fragments.put(position, fragment)
            }

            1 -> {
                fragment = WatchNowFragment.newInstance()
                fragments.put(position, fragment)
            }

            2 -> {
                fragment = HotPicksFragment.newInstance()
                fragments.put(position, fragment)
            }
        }

        return fragment
    }

    fun getCurrentFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int = 3

}