package com.funnywolf.littledemon.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

class DragCloseLayout: FrameLayout {
    private var canScrollView: View? = null
    private var onCloseListener: OnCloseListener? = null
    private var downRawY = 0.0f
    private var lastRawY = 0.0f

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) {
            return super.onInterceptTouchEvent(ev)
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downRawY = ev.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                if (shouldHandleEvent((downRawY - ev.rawY).toInt())) {
                    return true
                }
            }
        }
        lastRawY = ev.rawY
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downRawY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                scrollBy(0, (lastRawY - event.rawY).toInt())
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (Math.abs(downRawY - event.rawY) > measuredHeight / 5) {
                    if (onCloseListener?.onClose() != true) {
                        scrollTo(0, 0)
                    }
                } else {
                    scrollTo(0, 0)
                }
            }
        }
        lastRawY = event.rawY
        return true
    }

    private fun shouldHandleEvent(dy: Int): Boolean {
        return (canScrollView?.canScrollVertically(dy) != true)
    }

    fun setOnCloseListener(listener: (() -> Boolean)?) {
        onCloseListener = object : OnCloseListener {
            override fun onClose(): Boolean {
                return listener?.invoke() ?: false
            }
        }
    }

    fun setCanScrollView(v: View?) {
        canScrollView = v
    }

    interface OnCloseListener {
        fun onClose(): Boolean
    }
}