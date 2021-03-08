package com.lge.lgshoptimem.ui.more

import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lge.lgshoptimem.ui.common.AppConst

class ForYouSettingFragmentAdapter(var fragmentActivity: ForYouSettingActivity): FragmentStateAdapter(fragmentActivity) {
    private val fragments = SparseArray<Fragment>()

    override fun createFragment(position: Int): Fragment {
        lateinit var fragment: Fragment
        val bundle: Bundle = Bundle()
        bundle.putInt(AppConst.KEY.LAUNCH_FROM, fragmentActivity.mLaunchFrom)

        when (position) {
            0 -> {
                fragment = ForYouSettingCategoryFragment.newInstance()
                fragment.arguments = bundle
                fragments.put(position, fragment)
            }

            1 -> {
                fragment = ForYouSettingKeywordFragment.newInstance()
                fragment.arguments = bundle
                fragments.put(position, fragment)
            }
        }

        return fragment
    }

    fun getCurrentFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int = 2

}