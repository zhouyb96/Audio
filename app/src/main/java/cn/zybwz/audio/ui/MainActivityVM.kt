package cn.zybwz.audio.ui

import androidx.lifecycle.MutableLiveData
import cn.zybwz.audio.bean.RecordBean
import cn.zybwz.audio.db.AudioDataBase
import cn.zybwz.base.BaseViewModel

class MainActivityVM:BaseViewModel() {
    val recordStatusData = MutableLiveData(0)

    fun insertRecord(recordBean: RecordBean){
        launchIo({
            AudioDataBase.getInstance().recordDao().insertRecord(recordBean)
        })
    }
}