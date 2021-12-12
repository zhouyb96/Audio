package cn.zybwz.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel:ViewModel() {
    val showLoading:MutableLiveData<Boolean> = MutableLiveData()
}