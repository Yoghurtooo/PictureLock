package com.ycw.piclock2

import android.graphics.Point
import android.graphics.Rect
import android.hardware.input.InputManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val dots:Array<ImageView> by lazy {
        return@lazy arrayOf(dot1,dot2,dot3,dot4,dot5,dot6,dot7,dot8,dot9)
    }

    //保存被选择的view
    private val allSelectedDots = mutableListOf<ImageView>()
    //保存被选择的线
    private val allSelectedLine = mutableListOf<ImageView>()

    //用于记录滑动轨迹
    private val password = StringBuilder()

    //保存所有线的tag值
    private val allLineTags = arrayOf(
        12,23,45,56,78,89,      //横线
        14,25,36,47,58,69,      //竖线
        24,35,57,68,15,26,48,59 //斜线
    )

    //记录上一个被点亮的圆点对象
    private var lastSeletcedView: ImageView? = null

    //顶部高度
    private val barHeight: Int by lazy{
        val display = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(display)

        val drawingRect = Rect()
        window.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT).getDrawingRect(drawingRect)

        display.heightPixels - drawingRect.height()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                findViewContaninsPoint(converTouchLocationToContainer(event)).also {
                    highlightView(it)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                findViewContaninsPoint(converTouchLocationToContainer(event)).also {
                    highlightView(it)
                }
            }
            MotionEvent.ACTION_UP -> {
                reSet()
            }
        }
        return true
    }

    //点亮view
    private fun highlightView(v: ImageView?) {
        if (v != null && v.visibility == View.VISIBLE) {
            v.visibility = View.INVISIBLE
            allSelectedDots.add(v)
            password.append(v.tag)

            //判断是不是第一个点
            if (lastSeletcedView != null) {
                //获取线tag值
                val previous = lastSeletcedView?.tag.toString().toInt()
                val current = v.tag.toString().toInt()
                val lineTag =
                    if (previous > current) current * 10 + previous else previous * 10 + current

                //判断是否有这个线
                if (allLineTags.contains(lineTag)) {
                    //点亮这个线
                    mContainer.findViewWithTag<ImageView>(lineTag.toString()).apply {
                        visibility = View.VISIBLE
                        allSelectedLine.add(this)
                    }
                }
            }
            //记录
            lastSeletcedView = v
        }
    }

    //还原操作
    private fun reSet(){
        //遍历数组
        for (view in allSelectedDots){
            view.visibility = View.VISIBLE
        }
        for (view in allSelectedLine){
            view.visibility = View.INVISIBLE
        }
        allSelectedDots.clear()
        lastSeletcedView = null
        Log.v("test",password.toString())
        password.clear()
    }


    //触摸点坐标转化为相对于容器的坐标
    private fun converTouchLocationToContainer(event: MotionEvent): Point {
        return Point().apply {
            x = (event.x - mContainer.x).toInt()
            y = (event.y - barHeight - mContainer.y).toInt()
        }
    }

    //获取触摸点所在的圆点视点
    private fun findViewContaninsPoint(point: Point): ImageView?{
        //遍历所有的点
        for (dotView in dots){
            //判断这个视图是否包含point
            getRectForView(dotView).also {
                if(it.contains(point.x,point.y)){
                    return dotView
                }
            }
        }
            return null
    }

    //获取控件的Rect
    private fun getRectForView(v: ImageView) = Rect(v.left,v.top,v.right,v.bottom)
}
