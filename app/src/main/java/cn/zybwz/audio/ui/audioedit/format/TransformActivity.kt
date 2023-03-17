package cn.zybwz.audio.ui.audioedit.format

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.zybwz.audio.R
import cn.zybwz.audio.databinding.ActivityTransformBinding
import cn.zybwz.base.BaseActivity
import cn.zybwz.binmedia.FFmpegCmd
import cn.zybwz.binmedia.bean.AudioInfo
import com.google.android.material.tabs.TabLayout
import java.io.File

class TransformActivity : BaseActivity<TransformActivityVM,ActivityTransformBinding>() {

    private var originFile:String?=null
    override val viewModel: TransformActivityVM by viewModels()
    private var sinkInfo:AudioInfo= AudioInfo()
    private var fileType=0//0wav 1mp3 2aac

    override fun bindLayout(): Int = R.layout.activity_transform

    override fun titleBar(): View {
        binding.ivBack.setOnClickListener {
            finish()
        }
        return binding.titleBar
    }

    override fun initViewModel() {

    }

    override fun initView() {

        binding.tabType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        binding.tabBit.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        binding.tabBit.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        binding.tabType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private var readInfo:AudioInfo?=null
    override fun initData() {
        originFile = intent.getStringExtra("path")
        //"/storage/emulated/0/Android/data/cn.zybwz.audio/files/recorder/20220512_102410.mp3"
        originFile?.let {path->
            readInfo =
                FFmpegCmd().readInfo(path)
            readInfo?.let {info->
                binding.srcType.text="类型:"+"MP3";
                binding.srcHz.text="频率:${info.sampleRate}";
                val channel=if (info.channels==2)
                    "立体声" else "单声道"
                binding.srcChannels.text="声道:$channel";
                binding.srcDuration.text="时长:${info.duration}";
                binding.srcPath.text=originFile
                binding.srcBitRate.text="精度:${info.bitFormat}位"
                binding.srcSize.text="大小${(File(path).length())}"
                Log.e(TAG, "initData: "+info.sampleRate )
            }

        }

    }
}