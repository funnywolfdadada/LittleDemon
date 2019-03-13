package com.funnywolf.littledemon.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.utils.getScreenHeight
import kotlinx.android.synthetic.main.layout_drag_extend.view.*

class DragExtentLayout: FrameLayout {

    constructor(context: Context): super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.layout_drag_extend, this, true)
//        bottomLayout.setBackgroundResource(R.mipmap.bg0)
        topLayout.setBackgroundResource(R.mipmap.bg1)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        topLayout.y = getScreenHeight(context) / 5.0f * 4.0f
        topLayout.setOnTouchListener(OnTouchTopLayout(topLayout.y))
    }

    private inner class OnTouchTopLayout(val initY: Float): OnTouchListener {
        private var downRawY = 0.0f
        private var lastRawY = 0.0f
        private var dy = 0.0f
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action ?: return false) {
                MotionEvent.ACTION_DOWN -> {
                    downRawY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    dy = event.rawY - lastRawY
                    topLayout.y = canMoveY(topLayout.y + dy * 0.5f)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (dy > 0) {
                        topLayout.animate().translationY(initY).setDuration(100).start()
                    } else {
                        topLayout.animate().translationY(0.0f).setDuration(100).start()
                    }
                    Log.d(DragExtentLayout::javaClass.name, "onTouch: $dy")
                }
            }
            lastRawY = event.rawY
            return true
        }

        private fun canMoveY(newY: Float): Float {
            if (newY < 0) {
                return 0.0f
            }
            return newY
        }
    }

}