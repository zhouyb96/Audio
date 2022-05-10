package cn.zybwz.binmedia.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.ArrayList


open class WaveView(context: Context,attributeSet: AttributeSet):BaseEditView(context,attributeSet){
    companion object{
        const val TYPE_RECORDING=0;
        const val TYPE_PLAYING=1;
    }

    private val waveLinePaint=Paint()


    val waveList:MutableList<Byte> = mutableListOf()

    private var type=TYPE_RECORDING //

    init {
        waveLinePaint.color= Color.BLACK
        waveLinePaint.strokeWidth=6f
        waveLinePaint.isAntiAlias=true
        waveLinePaint.style=Paint.Style.FILL
//        //todo 模拟分贝列表 得具体处理
//        for (i in 0 .. 20){
//            waveList.add(((i*10/2.00).toInt()))
//        }
        setBackgroundColor(Color.parseColor("#eeeeee"))
    }


    fun setType(type:Int){
        this.type=type
        setCurrentTime(0)
    }

    private fun drawWave(canvas: Canvas?){
        val start=if(((getCurrentTime()- pxTime/2)/200)>0)
            (getCurrentTime()- pxTime/2)/200
        else 0
        val end=if(getCurrentTime()/200>30)
            start+30
        else getCurrentTime()/200
        if (waveList.isEmpty())
            return
        for (i in start.toInt() until  end.toInt()){
            val l = (i*200) - pxTime/2
            val toFloat = ((l-getCurrentTime()) * msPerPx + width).toFloat()
            val lineHeight=(waveList[i]-40)*10f//todo 处理真实dp转化

            canvas?.drawLine(toFloat,(height)/2-lineHeight/2,toFloat,(height)/2+lineHeight/2,waveLinePaint)
        }
    }

    private fun drawPlayWave(canvas: Canvas?){
        val start=if(((getCurrentTime()- pxTime/2)/200)>0)
            (getCurrentTime()- pxTime/2)/200
        else 0
        var end=(getCurrentTime()+6000)/200
        if (end>waveList.size)
            end=waveList.size.toLong()
        if (waveList.isEmpty())
            return
        for (i in start.toInt() until  end.toInt()){
            val l = (i*200) - pxTime/2
            val toFloat = ((l-getCurrentTime()) * msPerPx + width).toFloat()
            val lineHeight=(waveList[i]-40)*10f//todo 处理真实dp转化

            canvas?.drawLine(toFloat,(height)/2-lineHeight/2,toFloat,(height)/2+lineHeight/2,waveLinePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (type== TYPE_RECORDING)
            return true
        else {
            return  super.onTouchEvent(event)
            }
    }

    override fun drawCustom(canvas: Canvas?) {
        if (type== TYPE_RECORDING)
        drawWave(canvas)
        else drawPlayWave(canvas)
    }

    interface TouchEvent{
        fun onTouchDown()

        fun onProgress(progress:Long)

        fun onTouchUp()
    }
}