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
import cn.zybwz.audio.ui.audioedit.format.TransformActivity
import cn.zybwz.audio.ui.vosk.VoskFragment
import cn.zybwz.audio.utils.ms2Format
import cn.zybwz.audio.view.AudioInfoDialog
import cn.zybwz.audio.view.FormatRenameDialog
import cn.zybwz.base.BaseActivity
import cn.zybwz.base.utils.ToastUtil
import cn.zybwz.binmedia.BinPlayer
import cn.zybwz.binmedia.FFmpegCmd
import cn.zybwz.binmedia.FilterUtil
import cn.zybwz.binmedia.bean.AudioInfo
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
    private var fFmpegCmd = FFmpegCmd()
    private var readInfo:AudioInfo?=null
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

    private val audioInfoDialog by lazy {
        val dialog = AudioInfoDialog(this)
        dialog
    }
    override fun initView() {
        val s = intent.getSerializableExtra(PLAY_MUSIC)?:return
        audioBean = s as RecordBean
        binding.tvTitle.text=audioBean.name
        binding.tvTime.text=simpleDateFormat.format(audioBean.date)
        binding.tvTotalDuration.text= ms2Format(audioBean.duration/10)
        binding.event=this
        binding.waveView.setType(WaveView.TYPE_PLAYING)
        Thread{
            val wave = fFmpegCmd.getWave(audioBean.path, 5)


            runOnUiThread {
                binding.waveView.waveList.addAll(wave.toMutableList())
                binding.waveView.postInvalidate()
                readInfo = FFmpegCmd().readInfo(audioBean.path)
            }

        }.start()
        binding.waveView.maxDuration=audioBean.duration
        binding.waveView.touchEvent= object :WaveView.TouchEvent{
            override fun onTouchDown() {
                //binPlayer.pause()
                if (viewModel.playStatusData.value==1)
                    binPlayer.pause()
            }

            override fun onProgress(progress: Long) {
                currentDuration=progress
                if (viewModel.playStatusData.value!=1)
                    binPlayer.seek(currentDuration)
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
//                    binPlayer.seek(currentDuration)
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
        currentDuration=l
        binding.tvPlayDuration.text=ms2Format(currentDuration/10)
        binding.waveView.setCurrentTime(l)
    }

    override fun onForward(view: View) {
        var l = currentDuration + 3000
        Log.e(TAG, "onForward: $l $currentDuration ${audioBean.duration}", )
        if (l>audioBean.duration)
            l=audioBean.duration
        binPlayer.seek(l)
        currentDuration=l
        binding.tvPlayDuration.text=ms2Format(currentDuration/10)
        binding.waveView.setCurrentTime(l)
    }

    private var navigationLevel=0
    private val formatRenameDialog by lazy {
        val dialog = FormatRenameDialog(this@AudioPlayActivity)
        dialog.event=object : FormatRenameDialog.Event{
            override fun onConfirm(name: String,type:String) {
                val fFmpegCmd = FFmpegCmd()
                when(type){
                    "MP3"->{

                    }
                    "AAC"->{
                        fFmpegCmd.mp32AAC(audioBean.path,audioBean.path.replace(audioBean.name,name))
                    }
                    "WAV"->{
                        fFmpegCmd.mp32Wav(audioBean.path,audioBean.path.replace(audioBean.name,name))
                    }
                }
                dialog.dismiss()
            }

        }
        dialog
    }
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
//                    val intent = Intent(this@AudioPlayActivity, TransformActivity::class.java)
//                    intent.putExtra("path",audioBean.path)
//                    startActivity(intent)
//                    return
                    editTool.addData(viewModel.getFormatTool())
                    editTool.event=object :ToolAdapter.Event{
                        override fun onClick(toolBean: ToolBean, position: Int) {
                            var fileName=""
                            when(position){
                                0->{
                                    fileName=audioBean.name.substring(0,audioBean.name.indexOfLast { it=='.'})+".mp3"
                                }
                                1->{
                                    fileName=audioBean.name.substring(0,audioBean.name.indexOfLast { it=='.'})+".aac"
                                }
                                2->{
                                    fileName=audioBean.name.substring(0,audioBean.name.indexOfLast { it=='.'})+".wav"
                                }
                            }
                            formatRenameDialog.show()
                            formatRenameDialog.setName(fileName)
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

    override fun onInfo(view: View) {

        audioInfoDialog.show()
        readInfo?.let {
            audioInfoDialog.setAudioInfo(audioBean.path,it)
        }
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
                currentDuration=it
            }
            //Log.e(TAG, "initData: $it")
        }
    }

    override fun onDestroy() {
        binPlayer.seek(audioBean.duration)
        binPlayer.destroy()

        super.onDestroy()
    }
}