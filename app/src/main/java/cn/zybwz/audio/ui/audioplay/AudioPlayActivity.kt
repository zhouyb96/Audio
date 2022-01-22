package cn.zybwz.audio.ui.audioplay

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import cn.zybwz.audio.R
import cn.zybwz.audio.bean.RecordBean
import cn.zybwz.audio.databinding.ActivityAudioPlayBinding
import cn.zybwz.audio.ui.audioedit.crop.AudioCropActivity
import cn.zybwz.audio.utils.ms2Format
import cn.zybwz.base.BaseActivity
import cn.zybwz.binmedia.BinPlayer
import cn.zybwz.binmedia.FFmpegCmd
import cn.zybwz.binmedia.widget.WaveView
import java.text.SimpleDateFormat

class AudioPlayActivity : BaseActivity<AudioPlayActivityVM,ActivityAudioPlayBinding>(),IAudioPlayEvent,IAudioEvent {
    private lateinit var audioBean: RecordBean
    private val simpleDateFormat= SimpleDateFormat("yyyy年MM月dd日")
    private val binPlayer = BinPlayer()
    override val viewModel: AudioPlayActivityVM by viewModels()
    private var currentDuration=0L
    private var needSeek=false
    override fun bindLayout(): Int = R.layout.activity_audio_play

    override fun titleBar(): View? {
        binding.ivBack.setOnClickListener {
            finish()
        }
        return binding.titleBar
    }

    override fun initViewModel() {

    }

    override fun initView() {
        val s = intent.getSerializableExtra(PLAY_MUSIC)?:return
        audioBean = s as RecordBean
        binding.tvTitle.text=audioBean.name
        binding.tvTime.text=simpleDateFormat.format(audioBean.date)
        binding.tvTotalDuration.text= ms2Format(audioBean.duration/10)
        binding.event=this
        binding.waveView.setType(WaveView.TYPE_PLAYING)
        binding.waveView.maxDuration=audioBean.duration
        binding.waveView.touchEvent= object :WaveView.TouchEvent{
            override fun onTouchDown() {
                //binPlayer.pause()
                if (viewModel.playStatusData.value==1)
                    binPlayer.pause()
            }

            override fun onProgress(progress: Long) {
                currentDuration=progress
                binding.tvPlayDuration.text=ms2Format(progress/10)
                needSeek=true
            }

            override fun onTouchUp() {

            }

        }
        binding.viewmodel=viewModel
    }

    companion object{
        private const val PLAY_MUSIC="play_music";
        fun startActivity(context:Context,audioBean: RecordBean){
            val intent = Intent(context, AudioPlayActivity::class.java)
            intent.putExtra(PLAY_MUSIC,audioBean)
            context.startActivity(intent)
        }
    }

    override fun onControl(view: View) {
        when(viewModel.playStatusData.value){
            0->{
                ///storage/emulated/0/Android/data/cn.zybwz.audio/files/recorder/tlbb.mp3
                binPlayer.play(audioBean.path)
                if (needSeek){
                    binPlayer.pause()
                    binPlayer.seek(currentDuration)
                    binPlayer.resume()
                }
            }
            1->{
                binPlayer.pause()
            }
            2->{
                binPlayer.resume()
                if (needSeek){
                    binPlayer.seek(currentDuration)
                }
            }
        }
    }

    override fun onBack(view: View) {
        var l = currentDuration - 3000
        if (l<0)
            l=0
        binPlayer.seek(l)
    }

    override fun onForward(view: View) {
        var l = currentDuration + 3000

        if (l>audioBean.duration)
            l=audioBean.duration
        binPlayer.seek(l)
    }

    override fun onCrop(view: View) {

    }

    override fun onFilter(view: View) {

    }

    override fun onFade(view: View) {

    }

    override fun onFormat(view: View) {

    }

    private val playListener = object : BinPlayer.IStatusChangeListener {
        override fun onStart() {
            Log.e(TAG, "onStart: ", )
            runOnUiThread {
                viewModel.playStatusData.value=1
            }

        }

        override fun onPause() {
            Log.e(TAG, "onPause: ", )
            runOnUiThread {
                viewModel.playStatusData.value=2
            }

        }

        override fun onStop() {
            Log.e(TAG, "onStop: ", )
            runOnUiThread {
                viewModel.playStatusData.value=0
            }

        }

        override fun onError(errorCode: Int) {

        }

    }

    override fun initData() {
        binPlayer.addStatusListener(playListener)
        binPlayer.addProgressListener {
            Handler(Looper.getMainLooper()).post {
                binding.tvPlayDuration.text=ms2Format(it/10)
                binding.waveView.setCurrentTime(it)
                currentDuration=it*10
            }
            //Log.e(TAG, "initData: $it")
        }
    }

    override fun onDestroy() {
        binPlayer.stop()
        binPlayer.destroy()
        super.onDestroy()
    }
}