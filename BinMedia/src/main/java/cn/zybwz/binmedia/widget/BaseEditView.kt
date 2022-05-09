package cn.zybwz.binmedia.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

open class BaseEditView(context: Context, attributeSet: AttributeSet): View(context,attributeSet) {
    private val scaleTextPaint= Paint()
    private var duration:Long=10000L
    private var currentTime=0L
    private var pxTime=12000L

    private val largeScaleTime=2000

    private var msPerPx:Double=0.00
    private var rightX=0f
    init {
        scaleTextPaint.color= Color.BLACK
        scaleTextPaint.textSize=21f
        scaleTextPaint.isAntiAlias=true
        scaleTextPaint.style= Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rightX=w.toFloat()
        msPerPx=width.toDouble()/pxTime
        rightX=(duration/12000.00f)*width
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        drawScaleText(canvas)
    }

    private fun drawScaleText(canvas: Canvas?){
        val offset = currentTime % largeScaleTime
        for (i in 0..7){
            val l = ((currentTime-offset)+i*largeScaleTime)//
            val toFloat = ((l-currentTime) * msPerPx ).toFloat()
            for (j in 1..3){
                canvas?.drawLine(toFloat+(j*largeScaleTime*msPerPx/4).toFloat(),36f,toFloat+(j*largeScaleTime*msPerPx/4).toFloat(),51f,scaleTextPaint)
            }
            canvas?.drawLine(toFloat,36f,toFloat,66f,scaleTextPaint)
        }

    }

    fun setDuration(duration:Long){
        this.duration=duration
        if (duration>=12000){
            rightX=width.toFloat()
            this.pxTime=duration
        }else {
            rightX=(duration/12000.00f)*width
        }

        invalidate()
    }
}