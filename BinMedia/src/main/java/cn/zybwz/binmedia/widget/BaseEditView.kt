package cn.zybwz.binmedia.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * 音频编辑视图基类
 * 后续需要修改
 * 目前只支持到播放波形
 */
abstract class BaseEditView(context: Context, attributeSet: AttributeSet): View(context,attributeSet) {
    private val scaleTextPaint= Paint()
    private var duration:Long=0L
    private var currentTime=0L
    var pxTime=12000L

    private val largeScaleTime=2000
    private var currentTimeBuck=0L
    var msPerPx:Double=0.00
    private var rightX=0f
    var maxDuration=0L
    private val baselinePaint=Paint()
    var touchEvent: WaveView.TouchEvent?=null

    private val scaleTextOffset=21*5/4
    init {
        scaleTextPaint.color= Color.BLACK
        scaleTextPaint.textSize=21f
        scaleTextPaint.isAntiAlias=true
        scaleTextPaint.style= Paint.Style.STROKE

        baselinePaint.color= Color.RED
        baselinePaint.strokeWidth=3f
        baselinePaint.isAntiAlias=true
        baselinePaint.style=Paint.Style.STROKE

        setBackgroundColor(Color.parseColor("#eeeeee"))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rightX=w.toFloat()
        msPerPx=width.toDouble()/pxTime
        rightX=(duration/12000.00f)*width
    }

    override fun onDraw(canvas: Canvas?) {
        msPerPx=width.toDouble()/pxTime
        super.onDraw(canvas)
        canvas?:return
        drawScaleText(canvas)
        drawCustom(canvas)
        drawBaseline(canvas)
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

    private fun drawBaseline(canvas: Canvas?){
        canvas?.drawLine(width/2f,0f,width/2f,height.toFloat(),baselinePaint)
    }

    private var downX=0f
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val action = event?.action?:return super.onTouchEvent(event)
        when(action){
            MotionEvent.ACTION_DOWN->{
                touchEvent?.onTouchDown()
                downX=event.rawX
                currentTimeBuck=currentTime
            }
            MotionEvent.ACTION_MOVE->{
                val moveX=event.rawX-downX
                var l = currentTimeBuck - (moveX / msPerPx).toLong()
                if (l>maxDuration)
                    l=maxDuration
                else if (l<0)
                    l=0
                currentTime=l
                touchEvent?.onProgress(currentTime)
                invalidate()
            }
            MotionEvent.ACTION_UP->{
                touchEvent?.onTouchUp()
            }
        }
        return true

    }

    abstract fun drawCustom(canvas: Canvas?)

    fun setDuration(duration:Long){
        this.duration=duration
        this.maxDuration=duration
        if (duration>=12000){
            rightX=width.toFloat()
            this.pxTime=duration
        }else {
            rightX=(duration/12000.00f)*width
        }

        invalidate()
    }

    fun getDuration():Long{
        return duration
    }

    fun setCurrentTime(current:Long){
        currentTime=current
        invalidate()
    }

    fun getCurrentTime():Long{
        return currentTime
    }

    interface TouchEvent{
        fun onTouchDown()

        fun onProgress(progress:Long)

        fun onTouchUp()
    }
}