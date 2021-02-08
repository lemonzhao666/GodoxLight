package com.godox.light.view;

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

class HScrollView : HorizontalScrollView {
    var hScrollView : HScrollView? = null
    constructor (context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        hScrollView = this
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_MOVE -> {
                onScrollEndListener!!.startStroll()
            }
            MotionEvent.ACTION_UP ->{
                this.postDelayed(run,50)
            }
        }
        return super.onTouchEvent(ev)
    }

    private val run = object : Runnable {
        override fun run() {
            if(isfinishScroll()){
                onScrollEndListener!!.endStroll()
                this@HScrollView.hScrollView!!.removeCallbacks(this)
            }else{
                handler.postDelayed(this, 50)
            }
        }
    }

    fun isfinishScroll(): Boolean {
        var isfinish = false
        var scrollview = HorizontalScrollView::class.java
        var scrollField = scrollview.getDeclaredField("mScroller")
        scrollField.setAccessible(true)
        var scroller = scrollField.get(this)
        var overscroller = scrollField.type
        var finishField = overscroller.getMethod("isFinished")
        finishField.setAccessible(true);
        isfinish = finishField.invoke(scroller) as Boolean
        return isfinish
    }
    interface OnScrollEndListener{
        fun endStroll()
        fun startStroll()
    }
    var onScrollEndListener: OnScrollEndListener?= null
    fun setOnSCrollEndListener(onScrollEndListener: OnScrollEndListener){
        this.onScrollEndListener = onScrollEndListener
    }
}