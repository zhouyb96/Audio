package cn.zybwz.audio.ui.recordfiles

import cn.zybwz.audio.bean.RecordBean
import cn.zybwz.audio.db.AudioDataBase

@Deprecated("一个方法没啥用")
class RecordFilesLocalModel {
    fun getAllRecords():List<RecordBean>{
        return AudioDataBase.getInstance().recordDao().selectAllRecord()
    }
}