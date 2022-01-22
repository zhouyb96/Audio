package cn.zybwz.audio.utils

import cn.zybwz.audio.App
import java.io.File

object FileUtils {
    private val RECORDER_PATH="recorder"
    private val EDIT_PATH="edit"

    fun getRecorderPath():String?{
        return App.application.getExternalFilesDir(RECORDER_PATH)?.path
    }

    fun getEditPath():String?{
        return App.application.getExternalFilesDir(EDIT_PATH)?.path
    }

    fun deleteFile(path:String){
        File(path).delete()
    }


}