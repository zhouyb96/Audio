package cn.zybwz.audio.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.zybwz.audio.App
import cn.zybwz.audio.bean.RecordBean

@Database(entities = arrayOf(RecordBean::class), version = 1,exportSchema = false)
abstract class AudioDataBase:RoomDatabase() {
    abstract fun recordDao():RecordDao
    companion object {
        @Volatile
        private var INSTANCE: AudioDataBase? = null
        fun getInstance(): AudioDataBase {

            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance;
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(App.application, AudioDataBase::class.java, "table_record")
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}