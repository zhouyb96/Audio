package cn.zybwz.audio

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import cn.zybwz.audio.databinding.ActivityMainBinding
import cn.zybwz.base.BaseActivity
import cn.zybwz.binmedia.OpenSLRecorder
import java.util.*


class MainActivity : BaseActivity<MainActivityVM,ActivityMainBinding>(),IMainActivityEvent{
    private lateinit var openSLRecorder:OpenSLRecorder
    override val viewModel: MainActivityVM by viewModels()

    override fun bindLayout(): Int = R.layout.activity_main

    override fun initViewModel() {

    }

    override fun initView() {
        binding.event=this
        openSLRecorder=OpenSLRecorder()
        openSLRecorder.init()
        openSLRecorder.addProgressListener {
            Log.e(TAG, "initView: $it", )
        }
    }

    override fun onStart(view: View) {
        val path=applicationContext.getExternalFilesDir("recorder")?.path+"/"+Date().time+".pcm"
        openSLRecorder.start(path)
    }

    override fun onPause(view: View) {
        openSLRecorder.pause()
    }

    override fun onResume(view: View) {
        openSLRecorder.resume()
    }

    override fun onStop(view: View) {
        openSLRecorder.stop()
    }
}