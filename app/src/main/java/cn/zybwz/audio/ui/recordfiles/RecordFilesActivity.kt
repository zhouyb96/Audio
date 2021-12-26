package cn.zybwz.audio.ui.recordfiles

import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.zybwz.audio.R
import cn.zybwz.audio.adapter.RecordAdapter
import cn.zybwz.audio.databinding.ActivityRecordFilesBinding
import cn.zybwz.base.BaseActivity

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
        binding.recycler.adapter=recordAdapter
    }

    override fun titleBar(): View = binding.titleBar

}