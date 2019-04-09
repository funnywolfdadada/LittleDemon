package com.funnywolf.littledemon.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView

class DragExtendLayout: NestedScrollView {

    companion object {
        const val MOVE_RESISTANCE = 0.5f
        const val MOVE_THRESHOLD = 20
    }

    /**
     * 默认未展开状态下子 View 的高度。默认是 0，子 View 不会显露出来
     */
    private var extendHeight = 0.0f

    /**
     * 是否展开
     */
    private var isExtended = false

    /**
     * 是否自己处理触摸事件
     */
    private var handledTouchEvent = false

    private val dragState = DragState()

    constructor(context: Context): super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init()
    }

    /**
     * 初始化。主要是先把 View 隐藏掉，待测量完成后移动到相应位置再显示出来，以防屏幕闪烁
     */
    private fun init() {
//        visibility = View.INVISIBLE
    }

    /**
     * 回到默认位置。参数 animate 表示是否使用动画
     */
    private fun goToResetPosition(animate: Boolean) {
        if (animate) {
            animate().y(measuredHeight - extendHeight).start()
        } else {
            y = measuredHeight - extendHeight
        }
        isExtended = false
        visibility = View.VISIBLE
    }

    /**
     * 回到默认位置。参数 animate 表示是否使用动画
     */
    private fun goToExtendPosition(animate: Boolean) {
        if (animate) {
            animate().y(0.0f).start()
        } else {
            y = 0.0f
        }
        isExtended = true
        visibility = View.VISIBLE
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // 小心为上
        if (ev == null) {
            return super.dispatchTouchEvent(ev)
        }

        when {
            // DOWN 的时候无法确定状态，不拦截，做一些拖动相关的初始化工作
            ev.action == MotionEvent.ACTION_DOWN -> {
                handledTouchEvent = false
                dragState.start(ev)
            }

            // MOVE 的时候根据 shouldHandleTouchEvent 判断是否拦截。如果拦截，之后的一系列 MOVE 事件都要自己处理
            ev.action == MotionEvent.ACTION_MOVE -> {
                if (handledTouchEvent || shouldHandleTouchEvent(ev)) {
                    handledTouchEvent = true
                    onTouchMove(ev)
                }
                dragState.update(ev)
            }

            // UP 和 CANCEL 判断之前是否拦截，拦截的话就处理
            // 为了让子 View 不响应错误的点击事件，又能够处理其他滑动相关的事件，传递 CANCEL 事件
            else -> {
                if (handledTouchEvent) {
                    onTouchUpOrCancel(ev)
                    handledTouchEvent = false
                    ev.action = MotionEvent.ACTION_CANCEL
                }
            }
        }
        return handledTouchEvent || super.dispatchTouchEvent(ev)
    }

    /**
     * 在手指上下滑且超过一定阈值的前提下，子 View 未展开或者手指向下滑已经划不动的时候返回 true
     */
    private fun shouldHandleTouchEvent(ev: MotionEvent) =
        (Math.abs(ev.rawY - dragState.lastRawY) > Math.abs(ev.rawX - dragState.lastRawX))
                && (Math.abs(ev.rawY - dragState.downRawY) > MOVE_THRESHOLD)
                && (!isExtended || (ev.rawY - dragState.lastRawY > 0 && !canScrollVertically(-1)))

    /**
     * 处理移动事件。View 带有一定阻尼地跟着手指移动
     */
    private fun onTouchMove(ev: MotionEvent) {
        val newY = y + (ev.rawY - dragState.lastRawY) * MOVE_RESISTANCE
        y = when {
            newY < 0 -> 0.0f
            else -> newY
        }
    }

    /**
     * 处理手指抬起或取消事件。根据最后两次事件的方向，判断是回到默认状态，还是完全展开
     */
    private fun onTouchUpOrCancel(ev: MotionEvent) {
        if (ev.action == MotionEvent.ACTION_CANCEL || dragState.dragDir > 0) {
            goToResetPosition(true)
        } else {
            goToExtendPosition(true)
        }
    }

    /**
     * 设置默认未展开状态下的子 View 高度
     */
    fun setExtendHeight(height: Float) {
        // 用 post 以保证测量完成
        post {
            extendHeight = when {
                height < 0 -> 0.0f
                height > measuredHeight -> measuredHeight.toFloat()
                else -> height
            }
            goToResetPosition(true)
        }
    }

    class DragState {

        /**
         * 拖拽的方向：
         *      = 0 -> reset
         *      < 0 -> 手指向上
         *      > 0 -> 手指向下
         */
        var dragDir = 0
            private set

        var downRawY = 0.0f

        /**
         * 上一次点击事件的 rawX，在 onInterceptTouchEvent 或 onTouchEvent 的 ACTION_DOWN 事件中设置
         */
        var lastRawX = 0.0f
            private set

        /**
         * 上一次点击事件的 rawY，在 onInterceptTouchEvent 或 onTouchEvent 的 ACTION_DOWN 事件中设置
         */
        var lastRawY = 0.0f
            private set

        fun start(ev: MotionEvent) {
            dragDir = 0
            downRawY = ev.rawY
            lastRawX = ev.rawX
            lastRawY = ev.rawY
        }

        fun update(ev: MotionEvent) {
            val dy = ev.rawY - lastRawY
            dragDir = when {
                dy > 0 -> 1
                dy < 0 -> -1
                else -> 0
            }
            lastRawX = ev.rawX
            lastRawY = ev.rawY
        }

    }
}