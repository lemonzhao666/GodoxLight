package com.zlm.base.media

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton

class StatusButtom : ImageButton, View.OnClickListener {
    lateinit var array: Array<Int>
    var status = false
    lateinit var statusListener: StatusListener

    constructor (context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle){
        setOnClickListener(this)
    }

    fun setBgBackground(array: Array<Int>) {
        this.array = array
    }

    fun setBtnStatusListener(statusListener: StatusButtom.StatusListener) {
        this.statusListener = statusListener
    }

    override fun onClick(v: View?) {
        if (status == false) {
            status = true
            setBackgroundResource(array[1])
            statusListener.status(true)
        } else {
            status = false
            setBackgroundResource(array[0])
            statusListener.status(false)
        }
    }

    fun setBtnStatus(status:Boolean){
        this.status = status
        when(status){
            true -> setBackgroundResource(array[1])
            false -> setBackgroundResource(array[0])
        }
    }
     interface StatusListener {
        fun status(status: Boolean)
    }
}