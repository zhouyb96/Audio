package cn.zybwz.audio.adapter

import cn.zybwz.audio.R
import cn.zybwz.audio.bean.RecordBean
import cn.zybwz.audio.databinding.ItemRecordBinding
import cn.zybwz.audio.utils.ms2Format
import java.text.SimpleDateFormat

class RecordAdapter:BaseAdapter<RecordBean,ItemRecordBinding>() {
    override fun bindLayout(): Int = R.layout.item_record
    private val simpleDateFormat=SimpleDateFormat("yyyy年MM月dd日")

    override fun onBindViewHolder(holder: BaseViewHolder<ItemRecordBinding>, position: Int) {
        val recordBean = dataList[position]
        holder.binding?.recordDate?.text=simpleDateFormat.format(recordBean.date)
        holder.binding?.recordDuration?.text= ms2Format(recordBean.duration)
        holder.binding?.recordName?.text=recordBean.name
        holder.itemView.setOnClickListener {

        }
    }

    interface ItemClickListener{
        fun onItemClick(position: Int,recordBean: RecordBean);
    }
}