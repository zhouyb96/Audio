package cn.zybwz.audio

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import cn.zybwz.audio.databinding.ActivityMainBinding
import cn.zybwz.audio.utils.ms2Format
import cn.zybwz.base.BaseActivity
import cn.zybwz.binmedia.OpenSLRecorder
import java.util.*


class MainActivity : BaseActivity<MainActivityVM,ActivityMainBinding>(),IMainActivityEvent{
    private lateinit var openSLRecorder:OpenSLRecorder
    override val viewModel: MainActivityVM by viewModels()

    override fun bindLayout(): Int = R.layout.activity_main

    override fun initViewModel() {
        viewModel.recordStatusData.observe(this,{
            Log.e(TAG, "initViewModel: $it", )
            when(it){
                0->{
                    openSLRecorder.stop()
                }
                1->{
                    val path=applicationContext.getExternalFilesDir("recorder")?.path+"/"+Date().time+".wav"
                    openSLRecorder.start(path)
                    binding.tvPauseResume.text="暂停"
                    binding.ivPauseResume.background=getDrawable(R.drawable.ic_pause)
                    binding.llPauseResume.visibility=View.VISIBLE
                }
                2->{
                    binding.tvPauseResume.text="继续"
                    binding.ivPauseResume.background=getDrawable(R.drawable.ic_play)
                    openSLRecorder.pause()
                }
                3->{
                    binding.tvPauseResume.text="暂停"
                    binding.ivPauseResume.background=getDrawable(R.drawable.ic_pause)
                    openSLRecorder.resume()
                }
            }
        })
        binding.viewModel=viewModel
    }

    override fun initView() {
        binding.event=this

        openSLRecorder=OpenSLRecorder()
        openSLRecorder.init()
        openSLRecorder.addProgressListener {
            Handler(Looper.getMainLooper()).post {
                binding.tvRecorderMs.text=ms2Format(it)
            }

        }
    }

    override fun onStartOrStop(view: View) {

        viewModel.recordStatusData.value=if (viewModel.recordStatusData.value?:0==0)
            1 else 0
        Log.e(TAG, "onStartOrStop: ${viewModel.recordStatusData.value}", )
    }

    override fun onPauseOrResume(view: View) {
        val i = viewModel.recordStatusData.value ?: 0
        if (i==1||i==3){
            viewModel.recordStatusData.value=2
        }else if (i==2){
            viewModel.recordStatusData.value=3
        }
    }

    override fun onDestroy() {
        openSLRecorder.destroy()
        super.onDestroy()
    }
}