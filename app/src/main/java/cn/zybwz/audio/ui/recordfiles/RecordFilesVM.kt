package cn.zybwz.audio.ui.recordfiles

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.zybwz.audio.bean.RecordBean
import cn.zybwz.audio.db.AudioDataBase
import cn.zybwz.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RecordFilesVM:BaseViewModel() {
    val recordBeanList=MutableLiveData<List<RecordBean>>()
    /**
     * 暂时全部拿出来吧，分页再说
     */
    fun getAllRecords(){
        viewModelScope.launch(Dispatchers.IO){
            val selectAllRecord=AudioDataBase.getInstance().recordDao().selectAllRecord()
            withContext(Dispatchers.Main){
                recordBeanList.value=selectAllRecord
            }
        }
    }
}