package cn.zybwz.audio.ui.audioplay

import androidx.lifecycle.MutableLiveData
import cn.zybwz.audio.bean.RecordBean
import cn.zybwz.audio.db.AudioDataBase
import cn.zybwz.base.BaseViewModel

class AudioPlayActivityVM:BaseViewModel() {
    val playStatusData = MutableLiveData(0)
}