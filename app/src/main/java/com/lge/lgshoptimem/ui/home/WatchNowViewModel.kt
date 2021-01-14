package com.lge.lgshoptimem.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lge.lgshoptimem.model.dto.Curation

class WatchNowViewModel : ViewModel() {
    val mldDataList: MutableLiveData<ArrayList<Curation>> = MutableLiveData()
}