package cn.zybwz.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel:ViewModel() {
    val showLoading:MutableLiveData<Boolean> = MutableLiveData()

    fun launchIo(block:()->Unit,blockUI: () -> Unit={}){
        viewModelScope.launch(Dispatchers.IO){
            block()
            withContext(Dispatchers.Main){
                blockUI()
            }
        }
    }
}