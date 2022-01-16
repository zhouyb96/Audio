package cn.zybwz.audio.ui.audioedit.crop

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import cn.zybwz.audio.R
import cn.zybwz.audio.bean.RecordBean
import cn.zybwz.audio.databinding.ActivityAudioCropBinding
import cn.zybwz.audio.databinding.ActivityAudioPlayBinding
import cn.zybwz.audio.ui.audioplay.AudioPlayActivity
import cn.zybwz.audio.utils.ms2Format
import cn.zybwz.base.BaseActivity
import cn.zybwz.binmedia.FFmpegCmd
import cn.zybwz.binmedia.widget.CropView

class AudioCropActivity : BaseActivity<AudioCropActivityVM,ActivityAudioCropBinding>(),IAudioCropEvent {
    private lateinit var audioBean: RecordBean
    private var startTime=0L
    private var endTime=0L
    val fFmpegCmd = FFmpegCmd()
    companion object{
        private const val PLAY_MUSIC="play_music";
        fun startActivity(context: Context,audioBean: RecordBean){
            val intent = Intent(context, AudioCropActivity::class.java)
            intent.putExtra(PLAY_MUSIC,audioBean)
            context.startActivity(intent)
        }
    }

    override val viewModel: AudioCropActivityVM by viewModels()

    override fun bindLayout(): Int = R.layout.activity_audio_crop

    override fun titleBar(): View? {
        return binding.titleBar
    }

    override fun initViewModel() {

    }

    override fun initView() {
        val s = intent.getSerializableExtra(PLAY_MUSIC)?:return
        audioBean = s as RecordBean
        binding.cropView.setDuration(audioBean.duration*10)
        binding.cropView.cropEvent = object : CropView.CropEvent{
            override fun onLeft(duration: Long) {
                startTime=duration
                binding.tvStart.text="开始\n"+ms2Format(startTime/10)
            }

            override fun onRight(duration: Long) {
                endTime=duration
                binding.tvEnd.text= "结束\n"+ms2Format(endTime/10)
            }

        }
        binding.tvStart.text="开始\n"+ms2Format(0)
        binding.tvEnd.text= "结束\n"+ms2Format(audioBean.duration)
        binding.event=this
    }

    override fun initData() {

    }

    override fun onCrop(view: View) {
        val replace = audioBean.path.replace(".mp3", "crop.mp3")
        fFmpegCmd.crop(audioBean.path,startTime,endTime-startTime,replace)
    }
}