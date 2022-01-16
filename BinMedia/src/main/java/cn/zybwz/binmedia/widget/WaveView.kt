package cn.zybwz.binmedia.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.ArrayList


open class WaveView(context: Context,attributeSet: AttributeSet):View(context,attributeSet){
    companion object{
        const val TYPE_RECORDING=0;
        const val TYPE_PLAYING=1;
    }
    private val scaleTextPaint=Paint()
    private val baselinePaint=Paint()
    private val waveLinePaint=Paint()
    private var currentTime=0L
    private val pxTime=12000L
    private val largeScaleTime=2000L
    private var msPerPx:Double=0.00;
    private val scaleTextOffset=21*5/4

    private val waveList:ArrayList<Int> = ArrayList()

    private var type=TYPE_RECORDING //

    private var downX=0f


    init {
        scaleTextPaint.color= Color.BLACK
        scaleTextPaint.textSize=21f
        scaleTextPaint.isAntiAlias=true
        scaleTextPaint.style=Paint.Style.STROKE

        baselinePaint.color= Color.RED
        baselinePaint.strokeWidth=3f
        baselinePaint.isAntiAlias=true
        baselinePaint.style=Paint.Style.STROKE

        waveLinePaint.color= Color.BLACK
        waveLinePaint.strokeWidth=6f
        waveLinePaint.isAntiAlias=true
        waveLinePaint.style=Paint.Style.FILL
        //todo 模拟分贝列表 得具体处理
        for (i in 0 .. 20){
            waveList.add(((i*10/2.00).toInt()))
        }
        setBackgroundColor(Color.parseColor("#eeeeee"))
    }

    fun setCurrentTime(current:Long){
        currentTime=current
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        msPerPx=width.toDouble()/pxTime

        drawBaseline(canvas)
        drawScaleText(canvas)
        //drawWave(canvas)
    }

    private fun drawBaseline(canvas: Canvas?){
        canvas?.drawLine(width/2f,0f,width/2f,height.toFloat(),baselinePaint)
    }

    private fun drawScaleText(canvas: Canvas?){
        val offset = currentTime % largeScaleTime
        for (i in 0..7){
            val l = ((currentTime-offset)+i*largeScaleTime - pxTime/2)//
            val toFloat = ((l-currentTime) * msPerPx + width / 2f).toFloat()
            for (j in 1..3){
                canvas?.drawLine(toFloat+(j*largeScaleTime*msPerPx/4).toFloat(),36f,toFloat+(j*largeScaleTime*msPerPx/4).toFloat(),51f,scaleTextPaint)
            }
            canvas?.drawLine(toFloat,36f,toFloat,66f,scaleTextPaint)
            if (l>=0){
                val s=String.format("%02d:%02d",0,l/1000)
                canvas?.drawText(s, toFloat- scaleTextOffset ,30f,scaleTextPaint)
            }
        }
    }

    private fun drawWave(canvas: Canvas?){
        for ((i,db) in waveList.withIndex()){
            val toFloat=(width/2+i*200*msPerPx).toFloat()
            val lineHeight=db//todo 处理真实dp转化
            canvas?.drawLine(toFloat,166f-lineHeight/2,toFloat,166f+lineHeight,waveLinePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (type== TYPE_RECORDING)
            return super.onTouchEvent(event)
        else {
            val action = event?.action?:return super.onTouchEvent(event)
            when(action){
                MotionEvent.ACTION_DOWN->{
                    downX=event.rawX
                }
                MotionEvent.ACTION_MOVE->{
                    val moveX=event.rawX-downX
                }
                MotionEvent.ACTION_UP->{

                }
            }
            return true
        }
    }
}