package cn.zybwz.audio.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log


import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import cn.zybwz.audio.R
import cn.zybwz.binmedia.bean.AudioInfo
import java.io.File

class AudioInfoDialog(context: Context):Dialog(context,R.style.dialog_bottom_full) {

    private lateinit var srcType:TextView
    private lateinit var srcHz:TextView
    private lateinit var srcChannels:TextView
    private lateinit var srcDuration:TextView
    private lateinit var srcPath:TextView
    private lateinit var srcBitRate:TextView
    private lateinit var srcSize:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_audio_info)
        srcType=findViewById(R.id.src_type)
        srcHz=findViewById(R.id.src_hz)
        srcChannels=findViewById(R.id.src_channels)
        srcDuration=findViewById(R.id.src_duration)
        srcPath=findViewById(R.id.src_path)
        srcBitRate=findViewById(R.id.src_bit_rate)
        srcSize=findViewById(R.id.src_size)
        val window: Window? = window
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.share_animation)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
    }

    fun setAudioInfo(path:String,readInfo: AudioInfo){
        readInfo?.let {info->
            srcType.text="类型:"+"MP3";
            srcHz.text="频率:${info.sampleRate}";
            val channel=if (info.channels==2)
                "立体声" else "单声道"
            srcChannels.text="声道:$channel";
            srcDuration.text="时长:${info.duration}";
            srcPath.text="文件位置：$path"
            srcBitRate.text="精度:${info.bitFormat}位"
            srcSize.text="大小${(File(path).length())}"
        }

    }
}