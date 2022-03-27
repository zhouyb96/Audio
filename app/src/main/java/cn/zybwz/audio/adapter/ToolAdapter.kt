package cn.zybwz.audio.adapter

import cn.zybwz.audio.R
import cn.zybwz.audio.bean.FilterBean
import cn.zybwz.audio.bean.ToolBean
import cn.zybwz.audio.databinding.ItemFilterBinding
import cn.zybwz.audio.databinding.ItemToolBinding

class ToolAdapter:BaseAdapter<ToolBean,ItemToolBinding>() {
    override fun bindLayout(): Int = R.layout.item_tool
    var event:Event?=null
    override fun onBindViewHolder(holder: BaseViewHolder<ItemToolBinding>, position: Int) {
        val binding = holder.binding ?: return
        val toolBean = dataList[position]
        binding.iconFilter.setBackgroundResource(toolBean.icon)
        binding.tvName.text=toolBean.name
        holder.itemView.setOnClickListener {
            event?.onClick(toolBean, position)
        }
    }

    interface Event{
        fun onClick(toolBean: ToolBean,position: Int)
    }
}