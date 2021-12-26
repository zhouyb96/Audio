package cn.zybwz.audio.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cn.zybwz.audio.bean.RecordBean

@Dao
interface RecordDao {
    @Insert
    fun insertRecord(recordBean: RecordBean)

    @Query("select * from table_record")
    fun selectAllRecord():List<RecordBean>

    @Delete
    fun deleteRecord(recordBean: RecordBean)
}