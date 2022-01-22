package cn.zybwz.audio.ui

import android.Manifest
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import cn.zybwz.audio.R
import cn.zybwz.audio.bean.RecordBean
import cn.zybwz.audio.databinding.ActivityMainBinding
import cn.zybwz.audio.ui.recordfiles.RecordFilesActivity
import cn.zybwz.audio.utils.FileUtils
import cn.zybwz.audio.utils.ms2Format
import cn.zybwz.base.BaseActivity
import cn.zybwz.binmedia.FFmpegCmd
import cn.zybwz.binmedia.OpenSLRecorder
import com.permissionx.guolindev.PermissionX
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity<MainActivityVM,ActivityMainBinding>(), IMainActivityEvent {
    private lateinit var openSLRecorder:OpenSLRecorder
    override val viewModel: MainActivityVM by viewModels()
    private var currentRecord=RecordBean()
    private val simpleDateFormat= SimpleDateFormat("yyyyMMdd_hhmmss")

    override fun bindLayout(): Int = R.layout.activity_main

    override fun initViewModel() {
        viewModel.recordStatusData.observe(this,{
            Log.e(TAG, "initViewModel: $it", )
            when(it){
                0->{
                    if (currentRecord.path.isNotEmpty()){
                        openSLRecorder.stop()

                        val fFmpegCmd = FFmpegCmd()
                        val replace = currentRecord.path.replace(".pcm", ".mp3")
                        fFmpegCmd.pcm2Mp3(currentRecord.path,replace)
                        FileUtils.deleteFile(currentRecord.path)
                        currentRecord.name=currentRecord.name.replace(".pcm", ".mp3")
                        currentRecord.path=replace
                        viewModel.insertRecord(currentRecord)
                    }
                }
                1->{
                    currentRecord= RecordBean()
                    val date = Date().time
                    val name = "${simpleDateFormat.format(date)}.pcm"
                    val path=FileUtils.getRecorderPath()+"/"+name
                    binding.tvRecorderPath.text=name.replace(".pcm", ".mp3")
                    currentRecord.name=name
                    currentRecord.path=path
                    currentRecord.date=date
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
        openSLRecorder.addProgressListener (object : OpenSLRecorder.IProgressListener{
            override fun onProgress(recorderMs: Long) {
                Handler(Looper.getMainLooper()).post {
                    currentRecord.duration=recorderMs*10
                    binding.waveView.setCurrentTime(recorderMs*10)
                    binding.tvRecorderMs.text=ms2Format(recorderMs)
                }
            }

            override fun onWave(db: Char) {

            }

        })
        requestPermission()

    }

    private fun requestPermission(){
        PermissionX.init(this)
            .permissions(Manifest.permission.RECORD_AUDIO)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {

                } else {
                    Toast.makeText(this, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
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

    override fun onRecordFile(view: View) {
        startActivity(Intent(this,RecordFilesActivity::class.java))
    }

    override fun onDestroy() {
        openSLRecorder.destroy()
        super.onDestroy()
    }

    override fun titleBar(): View =binding.root
    override fun initData() {

    }
}