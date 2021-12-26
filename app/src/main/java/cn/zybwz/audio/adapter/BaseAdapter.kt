package cn.zybwz.audio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T,B:ViewDataBinding>:RecyclerView.Adapter<BaseAdapter.BaseViewHolder<B>>() {
    val dataList:MutableList<T> = mutableListOf()

    fun addData(data:MutableList<T>){
        this.dataList.clear()
        this.dataList.addAll(data)
        notifyDataSetChanged()
    }

    abstract fun bindLayout():Int

    class BaseViewHolder<B:ViewDataBinding>(view: View):RecyclerView.ViewHolder(view){
        val binding= DataBindingUtil.bind<B>(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<B> {
        val inflate = LayoutInflater.from(parent.context).inflate(bindLayout(), parent, false)
        return BaseViewHolder(inflate)
    }

    override fun getItemCount(): Int = dataList.size
}