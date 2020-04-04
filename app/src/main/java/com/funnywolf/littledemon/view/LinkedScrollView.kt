package com.funnywolf.littledemon.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.FrameLayout
import android.widget.Scroller
import androidx.core.view.*
import com.funnywolf.littledemon.utils.containsChild

/**
 * 实现顶部视图和底部视图联动滑动的 view
 *
 * @author https://github.com/funnywolfdadada
 * @since 2020/3/30
 */
class LinkedScrollView: FrameLayout, NestedScrollingParent2 {

    /**
     * 顶部视图容器
     */
    private val topContainer: FrameLayout = FrameLayout(context)

    /**
     * 顶部视图中的可滚动 view，当自身无法再向下滚动时，会把剩余的滚动分发给它
     */
    private var topScrollableView: View? = null


    /**
     * 底部视图容器
     */
    private val bottomContainer: FrameLayout = FrameLayout(context)

    /**
     * 底部视图中的可滚动 view，当自身无法再向上滚动时，会把剩余的滚动分发给它
     */
    private var bottomScrollableView: View? = null

    /**
     * y 轴的最大滚动范围 = 顶部视图高度 + 底部视图高度 - 自身的高度
     */
    private var maxScrollY = 0

    /**
     * 上次触摸事件的 y 值，用于处理自身的滑动事件
     */
    private var lastY = 0F

    /**
     * 主要用于计算 fling 后的滚动距离
     */
    private val scroller = Scroller(context)

    /**
     * 用于计算自身时的 y 轴速度，处理自身的 fling
     */
    private val velocityTracker = VelocityTracker.obtain()

    /**
     * fling 是一连串连续的滚动操作，这里需要暂存 fling 的 view
     */
    private var flingChild: View? = null

    /**
     * 暂存 fling 时上次的 y 轴位置，用以计算当前需要滚动的距离
     */
    private var lastFlingY = 0

    /**
     * 嵌滚滑动的帮助类
     */
    private val parentHelper = NestedScrollingParentHelper(this)

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    init {
        addView(topContainer, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        addView(bottomContainer, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    }

    fun setTopView(v: View, scrollableChild: View? = null) {
        topContainer.removeAllViews()
        topContainer.addView(v)
        topScrollableView = scrollableChild
        requestLayout()
    }

    fun setBottomView(v: View, scrollableChild: View? = null) {
        bottomContainer.removeAllViews()
        bottomContainer.addView(v)
        bottomScrollableView = scrollableChild
        requestLayout()
    }

    /**
     * 布局时，topContainer 在顶部，bottomContainer 紧挨着 topContainer 底部
     * 布局完还要计算下最大的滚动距离
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        topContainer.layout(0, 0, topContainer.measuredWidth, topContainer.measuredHeight)
        bottomContainer.layout(0, topContainer.measuredHeight, bottomContainer.measuredWidth,
            topContainer.measuredHeight + bottomContainer.measuredHeight)
        maxScrollY = topContainer.measuredHeight + bottomContainer.measuredHeight - height
    }

    override fun dispatchTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            // 手指按下就中止 fling 等滑动行为
            scroller.forceFinished(true)
        }
        return super.dispatchTouchEvent(e)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        return when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                // 手指按下时记录 y 轴初始位置
                lastY = e.y
                velocityTracker.clear()
                velocityTracker.addMovement(e)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                // 当 touch 事件没有子 view 处理，就需要自身处理，进行滚动
                val dScrollY = (lastY - e.y).toInt()
                dispatchScrollY(dScrollY)?.scrollBy(0, dScrollY)
                lastY = e.y
                velocityTracker.addMovement(e)
                true
            }
            MotionEvent.ACTION_UP -> {
                // 手指抬起时计算 y 轴速度，然后自身处理 fling
                velocityTracker.addMovement(e)
                velocityTracker.computeCurrentVelocity(1000)
                handleFling(-velocityTracker.yVelocity.toInt(), null)
                true
            }
            else -> super.onTouchEvent(e)
        }
    }

    /**
     * 这里只接收垂直方向的嵌套滚动
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        parentHelper.onNestedScrollAccepted(child, target, axes)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        parentHelper.onStopNestedScroll(target)
    }

    override fun getNestedScrollAxes(): Int {
        return parentHelper.nestedScrollAxes
    }

    /**
     * target 将要滚动时的回调。分发 y 轴的滚动量，如果需要自身滚就自身滚，不需要就不再处理
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dispatchScrollY(dy, target) == this) {
            consumed[1] = dy
            scrollBy(0, dy)
        }
    }

    /**
     * target 滚动后的回调。未消耗的滚动量，就需要自身滚动
     */
    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        scrollBy(0, dyUnconsumed)
    }

    /**
     * 根据需要滚动的 view target 和滚动方向，确定将滚动量具体传递给哪个 view
     * 向下滚动（子 view 整体上移）时优先级：target（在顶部 view 时） -> 自己 -> 底部 view
     * 向上滚动（子 view 整体下移）时优先级: target（在底部 view 时） -> 自己 -> 顶部 view
     *
     * @param dScrollY y 轴的滚动增量
     * @param target 需要滚动的子 view，null 时则是自身需要在滚动
     * @return 需要消耗该滚动量的 view
     */
    private fun dispatchScrollY(dScrollY: Int, target: View? = null): View? {
        return if (dScrollY > 0) {
            // 向下滚动（子 view 整体上移）
            if (target != null && topContainer.containsChild(target) && target.canScrollVertically(dScrollY)) {
                // target 在顶部 view 中，且自己还可以消耗滚动，就自己处理
                target
            } else if (canScrollVertically(dScrollY)) {
                // target 为空，或者不在顶部 view 中，或者 target 无法消耗该滚动量
                // 自己可以处理就自己处理
                this
            } else {
                // 最后轮到底部的可滚动 view 处理
                bottomScrollableView
            }
        } else {
            // 向上滚动（子 view 整体下移）
            if (target != null && bottomContainer.containsChild(target) && target.canScrollVertically(dScrollY)) {
                // target 在底部 view 中，且自己还可以消耗滚动，就自己处理
                target
            } else if (canScrollVertically(dScrollY)) {
                // target 为空，或者不在底部 view 中，或者 target 无法消耗该滚动量
                // 自己可以处理就自己处理
                this
            } else {
                // 最后轮到顶部的可滚动 view 处理
                topScrollableView
            }
        }
    }

    /**
     * 子 view 将要 fling 时的回调。这里拦截 y 轴的 fling 事件，自己处理
     */
    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        handleFling(velocityY.toInt(), target)
        return true
    }

    /**
     * 处理 fling，通过 scroller 计算 fling，暂存 fling 的初值和需要 fling 的 view
     */
    private fun handleFling(vy: Int, target: View?) {
        lastFlingY = 0
        scroller.fling(0, lastFlingY, 0, vy, 0, 0, Int.MIN_VALUE, Int.MAX_VALUE)
        flingChild = target
        invalidate()
    }

    /**
     * 计算 fling 的滚动量，并将其分发到真正需要处理的 view
     */
    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            val currentFlingY = scroller.currY
            val dScrollY = currentFlingY - lastFlingY
            dispatchScrollY(dScrollY, flingChild)?.scrollBy(0, dScrollY)
            lastFlingY = currentFlingY
            invalidate()
        } else {
            flingChild = null
        }
    }

    /**
     * 根据滚动范围，确定是否可以垂直滚动
     */
    override fun canScrollVertically(direction: Int): Boolean {
        return if (direction > 0) {
            scrollY < maxScrollY
        } else {
            scrollY > 0
        }
    }

    /**
     * 自身滚动，做范围限制
     */
    override fun scrollTo(x: Int, y: Int) {
        super.scrollTo(x, when {
            y < 0 -> 0
            y > maxScrollY -> maxScrollY
            else -> y
        })
    }

}