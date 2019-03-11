package com.funnywolf.littledemon.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class DragCloseLayout: FrameLayout {
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
                lastRawY = ev.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                if (shouldHandleEvent((ev.rawY - downRawY).toInt())) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                scrollBy(0, (lastRawY - event.rawY).toInt())
                lastRawY = event.rawY
                return true
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
        return super.onTouchEvent(event)
    }

    private fun shouldHandleEvent(dy: Int): Boolean {
        if (childCount <= 0) {
            return false
        }
        if (!getChildAt(0).canScrollVertically(-dy)) {
            return true
        }
        return false
    }

    fun setOnCloseListener(listener: () -> Boolean) {
        onCloseListener = object : OnCloseListener {
            override fun onClose(): Boolean {
                return listener()
            }
        }
    }

    interface OnCloseListener {
        fun onClose(): Boolean
    }
}