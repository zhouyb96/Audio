package cn.zybwz.audio.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import cn.zybwz.audio.R
import cn.zybwz.audio.bean.RecordBean
import cn.zybwz.audio.databinding.ItemRecordBinding
import cn.zybwz.audio.utils.ms2Format
import java.text.SimpleDateFormat

/**
 * todo item删除动画 全选删除
 */
class RecordAdapter:BaseAdapter<RecordBean,ItemRecordBinding>() {
    private val TAG="RecordAdapter"
    override fun bindLayout(): Int = R.layout.item_record
    private val simpleDateFormat=SimpleDateFormat("yyyy年MM月dd日")
    var itemClickListener:ItemClickListener?=null

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: BaseViewHolder<ItemRecordBinding>, position: Int) {
        val recordBean = dataList[position]
        holder.binding?.recordDate?.text=simpleDateFormat.format(recordBean.date)
        holder.binding?.recordDuration?.text= ms2Format(recordBean.duration/10)
        holder.binding?.recordName?.text=recordBean.name
        holder.binding?.llDelete?.setOnClickListener {
            itemClickListener?.onItemDelete(position, recordBean)
        }
        val gestureDetectorListener=object : GestureDetector.OnGestureListener {
            override fun onDown(p0: MotionEvent?): Boolean { return true }
            override fun onShowPress(p0: MotionEvent?) {}

            override fun onSingleTapUp(p0: MotionEvent?): Boolean {
                itemClickListener?.onItemClick(position,recordBean)
                return true
            }

            /**
             * 侧滑删除
             */
            override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
                val scrollWidth = holder.binding?.llDelete?.width ?: 0
                if (p2<0){
                    if (holder.itemView.scrollX<0)
                        return true
                    if (holder.itemView.scrollX<scrollWidth*3/4)
                        holder.itemView.scrollTo(0,0)
                    else
                    holder.itemView.scrollBy(p2.toInt(),0)
                }else {
                    if (holder.itemView.scrollX>scrollWidth)
                        return true
                    if (holder.itemView.scrollX>scrollWidth/4)
                        holder.itemView.scrollTo(scrollWidth,0)
                    else holder.itemView.scrollBy(p2.toInt(),0)
                }
                return true
            }

            override fun onLongPress(p0: MotionEvent?) {
                itemClickListener?.onItemLongClick(position,recordBean)
            }

            override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
                return false
            }

        }
        val gestureDetector = GestureDetector(holder.itemView.context, gestureDetectorListener)

        holder.itemView.setOnTouchListener { view, motionEvent ->

            return@setOnTouchListener gestureDetector.onTouchEvent(motionEvent)
        }
    }

    interface ItemClickListener{
        fun onItemClick(position: Int,recordBean: RecordBean)
        fun onItemDelete(position: Int,recordBean: RecordBean)
        fun onItemLongClick(position: Int,recordBean: RecordBean)
    }

}