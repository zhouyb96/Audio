package cn.zybwz.audio.ui.recordfiles

import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.zybwz.audio.R
import cn.zybwz.audio.adapter.RecordAdapter
import cn.zybwz.audio.bean.RecordBean
import cn.zybwz.audio.databinding.ActivityRecordFilesBinding
import cn.zybwz.audio.ui.audioplay.AudioPlayActivity
import cn.zybwz.base.BaseActivity
import cn.zybwz.binmedia.BinPlayer

class RecordFilesActivity : BaseActivity<RecordFilesVM,ActivityRecordFilesBinding>(){

    private val recordAdapter = RecordAdapter()

    override val viewModel: RecordFilesVM by viewModels()

    override fun bindLayout(): Int = R.layout.activity_record_files

    override fun initViewModel() {
        viewModel.recordBeanList.observe(this,{
            recordAdapter.addData(it.toMutableList())
        })
        viewModel.getAllRecords()
    }

    override fun initView() {

        binding.recycler.layoutManager= LinearLayoutManager(this)
        recordAdapter.itemClickListener=object : RecordAdapter.ItemClickListener{
            override fun onItemClick(position: Int, recordBean: RecordBean) {
                val tlbb="/storage/emulated/0/Android/data/cn.zybwz.audio/files/recorder/tlbb.mp3"
                AudioPlayActivity.startActivity(this@RecordFilesActivity,recordBean)
            }
        }
        binding.recycler.adapter=recordAdapter
    }

    override fun titleBar(): View = binding.titleBar
    override fun initData() {

    }

}