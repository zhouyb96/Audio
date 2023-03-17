package cn.zybwz.binmedia.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CropView(context: Context, attributeSet: AttributeSet):BaseEditView(context,attributeSet){
    private val paintBoardLine=Paint()
    private val paintBeCrop=Paint()
    private val scaleTextPaint=Paint()

    private var downX=0f
    private var leftX=0f
    private var rightX=0f
    private var dragType=0 //0 未进入拖拽模式 1 左拖拽 2 右拖拽

    var cropEvent:CropEvent?=null

    init {

        scaleTextPaint.color= Color.BLACK
        scaleTextPaint.textSize=21f
        scaleTextPaint.isAntiAlias=true
        scaleTextPaint.style= Paint.Style.STROKE

        paintBoardLine.color= Color.RED
        paintBoardLine.strokeWidth= 3f
        paintBoardLine.isAntiAlias= true
        paintBoardLine.style= Paint.Style.STROKE

        paintBeCrop.color= Color.parseColor("#80000000")
        paintBeCrop.strokeWidth= 3f
        paintBeCrop.isAntiAlias= true
        paintBeCrop.style= Paint.Style.FILL

    }

//    fun setDuration(duration:Long){
//        this.duration=duration
//        if (duration>=12000){
//            rightX=width.toFloat()
//            this.pxTime=duration
//        }else {
//            rightX=(duration/12000.00f)*width
//        }
//
//        invalidate()
//    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rightX=w.toFloat()
        msPerPx=width.toDouble()/pxTime
        rightX=(getDuration()/12000.00f)*width
    }

//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//        canvas?:return
//        drawBoardLine(canvas)
//        drawBeCrop(canvas)
//    }

    private fun drawBoardLine(canvas: Canvas){
        canvas.drawLine(leftX,0f,leftX,height.toFloat(),paintBoardLine)
        canvas.drawLine(rightX,0f,rightX,height.toFloat(),paintBoardLine)
    }

    private fun drawBeCrop(canvas: Canvas){
        val rectL = Rect(0, 0, leftX.toInt(), height)
        val rectR = Rect(rightX.toInt(), 0, (msPerPx*getDuration()).toInt(), height)
        canvas.drawRect(rectL,paintBeCrop)
        canvas.drawRect(rectR,paintBeCrop)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?:return super.onTouchEvent(event)
        when(event.action){
            MotionEvent.ACTION_DOWN->{
                downX=event.x
                if (downX<leftX+30&&downX>leftX-30){
                    dragType=1
                }
                if (downX<rightX+30&&downX>rightX-30){
                    dragType=2
                }
                if (dragType==0)
                    return super.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE->{

                when(dragType){
                    0->{

                    }
                    1->{
                        if (event.x>rightX)
                            return true
                        if (event.x<0)
                            leftX=0f
                        else leftX=event.x
                    }
                    2->{
                        if (event.x<leftX)
                            return true
                        if (event.x>msPerPx*getDuration())
                            rightX=(msPerPx*getDuration()).toFloat()
                        else rightX=event.x
                    }
                }
                if (dragType==1){
                    cropEvent?.onLeft((leftX/msPerPx).toLong())
                }else if (dragType==2) {
                    cropEvent?.onRight((rightX/msPerPx).toLong())
                }
                invalidate()
            }
            MotionEvent.ACTION_UP->{

                dragType=0
            }
        }
        return true
    }

    override fun drawCustom(canvas: Canvas?) {
        canvas?:return
        drawBoardLine(canvas)
        drawBeCrop(canvas)
    }

    interface CropEvent{
        fun onLeft(duration: Long)
        fun onRight(duration: Long)
    }
}