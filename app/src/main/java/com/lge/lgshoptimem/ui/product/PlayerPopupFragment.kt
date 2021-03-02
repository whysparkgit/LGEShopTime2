package com.lge.lgshoptimem.ui.product

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.FragmentPlayerPopupBinding
import com.lge.lgshoptimem.model.dto.Product
import com.lge.lgshoptimem.model.dto.WatchNow
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.component.ComponentItemListener

class PlayerPopupFragment : Fragment(), ComponentItemListener {
    private lateinit var mBinding: FragmentPlayerPopupBinding
    private val mViewModel: VideoPlayerViewModel by activityViewModels()
    private lateinit var mProducts: ArrayList<Product>

    companion object {
        fun newInstance() = PlayerPopupFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        mBinding = FragmentPlayerPopupBinding.inflate(inflater, container, false)
        mBinding.listener = this

        if (arguments == null) {
            mViewModel.mldVideoProduct.observe(viewLifecycleOwner, this::onDataListChanged)
        } else {
            Trace.debug(">> arguments is not null")
            mProducts = arguments?.getParcelableArrayList<Product>("products") as ArrayList<Product>
            Trace.debug(">> products.size = ${mProducts.size}")
            mBinding.compList.setItemList(mProducts)
            mBinding.compList.addItemListener(this)
        }

        return mBinding.root
    }

    private fun onDataListChanged(itemList: WatchNow.Response.Data) {
        Trace.debug("++ onDataListChanged()")

        if (itemList.productInfos.isNullOrEmpty()) return

        mProducts = arrayListOf()

        itemList.productInfos.forEach {
            mProducts.add(it)
        }

        mBinding.compList.setItemList(mProducts)
        mBinding.compList.addItemListener(this)
    }

    override fun onClick(v: View, pos: Int) {
        Trace.debug("++ onClick() v = ${v.id} pos = $pos")

        when (v.id) {
            R.id.iv_close -> {
                activity?.onBackPressed()
            }
        }
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
        Trace.debug("++ onItemClick() parent = ${parent.id} parentPos = $parentPos item = ${item.id} pos = $pos")

//        val intent = Intent(context, DetailActivity::class.java)
//        intent.putExtra(AppConst.KEY.PARTNER_ID, mProducts[pos].patnrId)
//        intent.putExtra(AppConst.KEY.PRODUCT_ID, mProducts[pos].prdtId)
//        intent.putExtra(AppConst.KEY.LAUNCH_FROM, AppConst.VALUE.PLAYER)
//        startActivity(intent)

        val bundle: Bundle = Bundle()
        bundle.putString(AppConst.KEY.PARTNER_ID, mProducts[pos].patnrId)
        bundle.putString(AppConst.KEY.PRODUCT_ID, mProducts[pos].prdtId)
        bundle.putBoolean(AppConst.KEY.LAUNCH_FROM, true)

        activity?.supportFragmentManager?.commit {
            if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
                setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            } else {
                setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
            }

            setReorderingAllowed(true)
            add<DetailFragment>(R.id.fc_product_detail, null, bundle)
        }
    }
}