package cn.zybwz.audio.adapter

import cn.zybwz.audio.R
import cn.zybwz.audio.bean.FilterBean
import cn.zybwz.audio.databinding.ItemFilterBinding

class FilterAdapter:BaseAdapter<FilterBean,ItemFilterBinding>() {
    override fun bindLayout(): Int = R.layout.item_filter
    var event:Event?=null
    override fun onBindViewHolder(holder: BaseViewHolder<ItemFilterBinding>, position: Int) {
        val binding = holder.binding ?: return
        val filterBean = dataList[position]
        binding.iconFilter.setBackgroundResource(filterBean.icon)
        binding.tvName.text=filterBean.name
        holder.itemView.setOnClickListener {
            event?.onClick(filterBean, position)
        }
    }

    interface Event{
        fun onClick(filterBean: FilterBean,position: Int)
    }
}