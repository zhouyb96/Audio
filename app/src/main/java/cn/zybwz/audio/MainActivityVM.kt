package cn.zybwz.audio

import androidx.lifecycle.MutableLiveData
import cn.zybwz.base.BaseViewModel

class MainActivityVM:BaseViewModel() {
    val recordStatusData = MutableLiveData(0)
}