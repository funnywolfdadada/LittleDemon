package com.funnywolf.littledemon.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

class DragCloseLayout: FrameLayout {
    private var canScrollView: View? = null
    private var onCloseListener: OnCloseListener? = null
    private var downRawY = 0.0f
    private var lastRawY = 0.0f
    private var dragDir = 0
    private var initScrollX = 0
    private var initScrollY = 0

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
                dragDir = 0
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
                dragDir = 0
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = (lastRawY - event.rawY).toInt()
                if (dragDir == 0) {
                    dragDir = dy
                    initScrollPosition()
                }
                scrollTo(0, canScrollY(dy))
                Log.d("Scroll", " $scrollY")
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (Math.abs(scrollY - initScrollY) > measuredHeight / 5) {
                    if (onCloseListener?.onClose() != true) {
                        backToInitPosition()
                    }
                } else {
                    backToInitPosition()
                }
            }
        }
        lastRawY = event.rawY
        return true
    }

    private fun shouldHandleEvent(dy: Int): Boolean {
        return (canScrollView?.canScrollVertically(dy) != true)
    }

    private fun canScrollY(dy: Int): Int {
        var newScrollY = scrollY + dy
        when {
            dragDir > 0 -> {
                if (newScrollY < 0) {
                    newScrollY = 0
                } else if (newScrollY > measuredHeight) {
                    newScrollY = measuredHeight
                }
            }
            dragDir < 0 -> {
                if (newScrollY > 0) {
                    newScrollY = 0
                } else if (newScrollY < -measuredHeight) {
                    newScrollY = -measuredHeight
                }
            }
            else -> newScrollY = 0
        }
        return newScrollY
    }

    private fun initScrollPosition() {
        initScrollX = scrollX
        initScrollY = scrollY
    }

    private fun backToInitPosition() {
        scrollTo(initScrollX, initScrollY)
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