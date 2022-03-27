package cn.zybwz.audio.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "table_record")
class RecordBean(
    ):Serializable{
    @PrimaryKey(autoGenerate = true)
    var id:Int=0

    @ColumnInfo var name:String=""
    @ColumnInfo var path:String=""
    @ColumnInfo var pcm_path:String=""
    @ColumnInfo var date:Long=0
    @ColumnInfo var duration:Long=0
}
