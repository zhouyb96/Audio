package cn.zybwz.audio.ui.audioplay

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import cn.zybwz.audio.R
import cn.zybwz.audio.adapter.ToolAdapter
import cn.zybwz.audio.bean.RecordBean
import cn.zybwz.audio.bean.ToolBean
import cn.zybwz.audio.databinding.ActivityAudioPlayBinding
import cn.zybwz.audio.ui.audioedit.crop.AudioCropActivity
import cn.zybwz.audio.ui.audioedit.filter.FilterFragment
import cn.zybwz.audio.ui.vosk.VoskFragment
import cn.zybwz.audio.utils.ms2Format
import cn.zybwz.base.BaseActivity
import cn.zybwz.base.utils.ToastUtil
import cn.zybwz.binmedia.BinPlayer
import cn.zybwz.binmedia.FilterUtil
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
    private val baseTool=ToolAdapter()
    private val editTool=ToolAdapter()
//    private  val audioFragment=AudioFragment()
    private var voskFragment:VoskFragment?=null
    private var filterFragment:FilterFragment?=null
    override fun titleBar(): View? {
        binding.ivBack.setOnClickListener {
            finish()
        }
        return binding.titleBar
    }

    override fun initViewModel() {
        viewModel.filterData.observe(this,{
            Log.e(TAG, "initViewModel:${it.type} " )
            binPlayer.addFilter(it.type)

        })
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
//        supportFragmentManager.beginTransaction().add(R.id.fragment,audioFragment).commitAllowingStateLoss()
        baseTool.addData(viewModel.getBaseTool())
        binding.navigation.layoutManager=GridLayoutManager(this,5)
        baseTool.event=object : ToolAdapter.Event{
            override fun onClick(toolBean: ToolBean, position: Int) {
                showNavigation(position+1)
            }

        }
        binding.childBack.setOnClickListener {
            showNavigation(0)
        }
        binding.navigation.adapter=baseTool

        binding.childTool.layoutManager=GridLayoutManager(this,5)
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

    private var navigationLevel=0
    private fun showNavigation(level:Int){
        navigationLevel=level
        if (navigationLevel==0){
            binding.navigation.visibility=View.VISIBLE
            binding.childNavigation.visibility=View.GONE
        }else{
            when(navigationLevel){
                1->{
                    editTool.addData(viewModel.getEditTool())
                    editTool.event=object :ToolAdapter.Event{
                        override fun onClick(toolBean: ToolBean, position: Int) {
                            when(position){
                                0->{
                                    AudioCropActivity.startActivity(this@AudioPlayActivity,audioBean)
                                }
                                1->{

                                }
                            }
                        }
                    }
                    binding.childTool.adapter=editTool
                }
                2->{
                    editTool.addData(viewModel.getFilterTool())
                    editTool.event=object :ToolAdapter.Event{
                        override fun onClick(toolBean: ToolBean, position: Int) {
                            viewModel.filterIndex=position-1
                            when(position){
                                0->{
                                    binPlayer.addFilter(-1)
                                }
                                else->{

                                    val filterInfo = FilterUtil.getLive(position-1)
                                    Log.e(TAG, "onClick: ${filterInfo?.buildLiveCmd()}", )
                                    if (filterInfo==null){
                                        ToastUtil.show("功能未实现")
                                        return
                                    }
                                    binPlayer.addFilter(-1)
                                    binPlayer.addLiveFilter(filterInfo)
//                                    val buildLiveCmd = filterInfo.buildLiveCmd()
//                                    if (buildLiveCmd.contains(",")){
//                                        val splitCmd = buildLiveCmd.split(",")
//                                        val splitName = filterInfo.name.split(",")
//                                        for ((i,cmd) in splitCmd.withIndex()){
//                                            binPlayer.addFilterCustom(splitName[i], cmd)
//                                        }
//
//                                    }else binPlayer.addFilterCustom(filterInfo.name, buildLiveCmd)
                                }
                            }
                        }
                    }
                    binding.childTool.adapter=editTool
                }
                3->{
                    editTool.addData(viewModel.getProcessTool())
                    editTool.event=object :ToolAdapter.Event{
                        override fun onClick(toolBean: ToolBean, position: Int) {
                            viewModel.filterIndex=position-1
                            when(position){
                                0->{
                                    binPlayer.addFilter(-1)
                                }
                                else->{
                                    val filterInfo = FilterUtil.getProcess(position-1)
                                    Log.e(TAG, "onClick: ${filterInfo?.buildLiveCmd()}", )
                                    if (filterInfo==null){
                                        ToastUtil.show("功能未实现")
                                        return
                                    }
                                    binPlayer.addLiveFilter(filterInfo)
                                }
                            }
                        }
                    }
                    binding.childTool.adapter=editTool
                }
                4->{
                    editTool.addData(viewModel.getFormatTool())
                    editTool.event=object :ToolAdapter.Event{
                        override fun onClick(toolBean: ToolBean, position: Int) {

                        }
                    }
                    binding.childTool.adapter=editTool
                }
                5->{
                    editTool.addData(viewModel.getTranslateTool())
                    editTool.event=object :ToolAdapter.Event{
                        override fun onClick(toolBean: ToolBean, position: Int) {

                        }
                    }
                    binding.childTool.adapter=editTool
                }
            }
            binding.navigation.visibility=View.GONE
            binding.childNavigation.visibility=View.VISIBLE
        }

    }

    override fun onCrop(view: View) {

    }

    override fun onFilter(view: View) {
        val begin = supportFragmentManager.beginTransaction()
//        begin.hide(audioFragment)

        if (filterFragment!=null){

            filterFragment?.let {

                if (!it.isVisible)
                    begin.show(it)
            }
        }else {
            filterFragment = FilterFragment.newInstance()
            filterFragment?.let{
                begin.add(R.id.fragment,it,"vosk")
            }

        }
        begin.commit()
    }

    override fun onFade(view: View) {
//        AudioFilterFragment
    }

    override fun onFormat(view: View) {

    }

    override fun onText(view: View) {
        val begin = supportFragmentManager.beginTransaction()
//        begin.hide(audioFragment)

        if (voskFragment!=null){

            voskFragment?.let {
                it.recognizeFile(audioBean.pcm_path)
                if (!it.isVisible)
                    begin.show(it)
            }
        }else {
            voskFragment = VoskFragment.newInstance(audioBean.pcm_path)
            voskFragment?.let{
                begin.add(R.id.fragment,it,"vosk")
            }

        }


        begin.commit()
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