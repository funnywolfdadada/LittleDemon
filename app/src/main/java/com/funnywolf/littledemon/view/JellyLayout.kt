package com.funnywolf.littledemon.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Scroller
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import kotlin.math.abs

/**
 * 果冻一般的弹性视图
 *
 * @author https://github.com/funnywolfdadada
 * @since 2020/4/4
 */
class JellyLayout : FrameLayout, NestedScrollingParent2 {

    private var topView: View? = null
    private var bottomView: View? = null
    private var leftView: View? = null
    private var rightView: View? = null

    /**
     * 当前滚动所在的区域，一次只支持在一个区域滚动
     */
    var currRegion = REGION_NONE
        private set

    /**
     * 上次触摸事件的 x 值，用于处理自身的滑动事件
     */
    private var lastX = 0F

    /**
     * 上次触摸事件的 y 值，用于处理自身的滑动事件
     */
    private var lastY = 0F

    /**
     * x 轴滚动的最小值 = -左边视图宽度
     */
    private var minScrollX = 0

    /**
     * x 轴滚动的最大值 = 右边视图宽度
     */
    private var maxScrollX = 0

    /**
     * y 轴滚动的最小值 = -顶部视图高度
     */
    private var minScrollY = 0

    /**
     * y 轴滚动的最大值 = 底部视图高度
     */
    private var maxScrollY = 0

    /**
     * 用来处理松手时的连续滚动
     */
    private val scroller = Scroller(context)

    private val listeners = ArrayList<Listener>(2)

    private val parentHelper = NestedScrollingParentHelper(this)

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    fun setTopView(v: View?, layoutWidth: Int, layoutHeight: Int): JellyLayout {
        removeView(topView)
        topView = v
        if (v != null) {
            addView(v, LayoutParams(layoutWidth, layoutHeight))
        }
        return this
    }

    fun setBottomView(v: View?, layoutWidth: Int, layoutHeight: Int): JellyLayout {
        removeView(bottomView)
        bottomView = v
        if (v != null) {
            addView(v, LayoutParams(layoutWidth, layoutHeight))
        }
        return this
    }

    fun setLeftView(v: View?, layoutWidth: Int, layoutHeight: Int): JellyLayout {
        removeView(leftView)
        leftView = v
        if (v != null) {
            addView(v, LayoutParams(layoutWidth, layoutHeight))
        }
        return this
    }

    fun setRightView(v: View?, layoutWidth: Int, layoutHeight: Int): JellyLayout {
        removeView(rightView)
        rightView = v
        if (v != null) {
            addView(v, LayoutParams(layoutWidth, layoutHeight))
        }
        return this
    }

    fun addListeners(listener: Listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        topView?.also {
            it.x = (width - it.width) / 2F
            it.y = -it.height.toFloat()
        }
        bottomView?.also {
            it.x = (width - it.width) / 2F
            it.y = height.toFloat()
        }
        leftView?.also {
            it.x = -it.width.toFloat()
            it.y = (height - it.height) / 2F
        }
        rightView?.also {
            it.x = width.toFloat()
            it.y = (height - it.height) / 2F
        }
        minScrollX = -(leftView?.width ?: 0)
        maxScrollX = rightView?.width ?: 0
        minScrollY = -(topView?.height ?: 0)
        maxScrollY = bottomView?.height ?: 0
    }

    override fun dispatchTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            // down 时复位下当前区域
            MotionEvent.ACTION_DOWN -> currRegion = REGION_NONE
            // up 或 cancel 时复位到原始位置
            // 在这里处理是因为自身可能并没有处理任何 touch 事件，也就不能在 onToucheEvent 中处理到 up 事件
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> resetScroll()
        }
        return super.dispatchTouchEvent(e)
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return when (e.action) {
            // down 时不拦截，但需要记录位置
            MotionEvent.ACTION_DOWN -> {
                lastX = e.x
                lastY = e.y
                false
            }
            // move 时需要根据移动量，判断是否要自己拦截
            MotionEvent.ACTION_MOVE -> {
                val horizontal = lastX != e.x
                val vertical = lastY != e.y
                lastX = e.x
                lastY = e.y
                if (horizontal || vertical) {
                    // 寻找可以处理事件的子 view，没有就拦截下来自己处理
                    getScrollableChildUnder(e.x, e.y, horizontal, vertical) == null
                } else {
                    // 没有发生移动就先不拦截
                    false
                }
            }
            else -> super.onInterceptTouchEvent(e)
        }
    }

    /**
     * 寻找在 x, y 处可以水平或垂直移动的子 view
     */
    private fun getScrollableChildUnder(x: Float, y: Float, horizontal: Boolean, vertical: Boolean): View? {
        for(i in 0 until childCount) {
            val v = getChildAt(i)
            // 先判断点击位置是否在 v 上
            if (x !in v.x..(v.x + v.width) || y !in v.y..(v.y + v.height)) {
                continue
            }
            if (
                // 判断 v 是否可以左右移动
                (horizontal && (v.canScrollHorizontally(1) || v.canScrollHorizontally(-1)))
                ||
                // 判断 v 是否可以上下移动
                (vertical && (v.canScrollVertically(1) || v.canScrollVertically(-1)))
            ) {
                return v
            }
        }
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        return when (e.action) {
            // down 不处理，之后的就无法处理
            MotionEvent.ACTION_DOWN -> true
            // move 时判断自身是否能够处理
            MotionEvent.ACTION_MOVE -> {
                val dx = (lastX - e.x).toInt()
                val dy = (lastY - e.y).toInt()
                lastX = e.x
                lastY = e.y
                if (canScrollHorizontally(dx) || canScrollVertically(dy)) {
                    // 自己可以处理就请求父 view 不要拦截事件
                    requestDisallowInterceptTouchEvent(true)
                    dispatchScroll(dx, dy)
                    true
                } else {
                    false
                }
            }
            else -> super.onTouchEvent(e)
        }
    }

    /**
     * 是否可以滚动取决于当前的滚动区域 [currRegion] 和要判断的方向:
     * [REGION_LEFT] -> 只能在 [[minScrollX], 0] 范围内滚动
     * [REGION_RIGHT] -> 只能在 [0, [maxScrollX]] 范围内滚动
     * [REGION_TOP] -> 只能在 [[minScrollY], 0] 范围内滚动
     * [REGION_BOTTOM] -> 只能在 [0, [maxScrollY]] 范围内滚动
     * [REGION_NONE] -> 判断水平时是在 [[minScrollX], [maxScrollX]] 范围内，垂直时在 [[minScrollY], [maxScrollY]]
     * [canScrollHorizontally]，[canScrollVertically] 和 [scrollTo] 都遵循这个规则
     */
    override fun canScrollHorizontally(direction: Int): Boolean {
        return when (currRegion) {
            REGION_LEFT -> if (direction > 0) {
                scrollX < 0
            } else {
                scrollX > minScrollX
            }
            REGION_RIGHT -> if (direction > 0) {
                scrollX < maxScrollX
            } else {
                scrollX > 0
            }
            REGION_NONE -> if (direction > 0) {
                scrollX < maxScrollX
            } else {
                scrollX > minScrollX
            }
            else -> false
        }
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return when (currRegion) {
            REGION_TOP -> if (direction > 0) {
                scrollY < 0
            } else {
                scrollY > minScrollY
            }
            REGION_BOTTOM -> if (direction > 0) {
                scrollY < maxScrollY
            } else {
                scrollY > 0
            }
            REGION_NONE -> if (direction > 0) {
                scrollY < maxScrollY
            } else {
                scrollY > minScrollY
            }
            else -> false
        }
    }

    /**
     * 根据当前的滚动区域限制滚动范围，当滚动区域无法确定时就不滚动
     */
    override fun scrollTo(x: Int, y: Int) {
        val xx = when(currRegion) {
            REGION_LEFT -> x.constrains(minScrollX, 0)
            REGION_RIGHT -> x.constrains(0, maxScrollX)
            else -> 0
        }
        val yy = when(currRegion) {
            REGION_TOP -> y.constrains(minScrollY, 0)
            REGION_BOTTOM -> y.constrains(0, maxScrollY)
            else -> 0
        }
        super.scrollTo(xx, yy)
    }

    /**
     * 当滚动位置发生变化时，分发滚动区域和对应方向上的滚动百分比
     */
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        listeners.forEach {
            it.onScrollChanged(currRegion, getScrollPercent())
        }
    }

    /**
     * 分发滚动量，当滚动区域未知时需要先确定滚动区域
     */
    private fun dispatchScroll(dScrollX: Int, dScrollY: Int) {
        when (currRegion) {
            REGION_NONE -> {
                currRegion = when {
                    abs(dScrollX) > abs(dScrollY) -> when {
                        !canScrollHorizontally(dScrollX) -> {
                            REGION_NONE
                        }
                        dScrollX > 0 -> {
                            REGION_RIGHT
                        }
                        else -> {
                            REGION_LEFT
                        }
                    }
                    dScrollY != 0 -> when {
                        !canScrollVertically(dScrollY) -> {
                            REGION_NONE
                        }
                        dScrollY > 0 -> {
                            REGION_BOTTOM
                        }
                        else -> {
                            REGION_TOP
                        }
                    }
                    else -> REGION_NONE
                }
                // 滚动区域确认后再执行滚动
                if (currRegion != REGION_NONE) {
                    dispatchScroll(dScrollX, dScrollY)
                }
            }
            REGION_TOP, REGION_BOTTOM -> {
                scrollBy(0, dScrollY / 2)
            }
            REGION_LEFT, REGION_RIGHT -> {
                scrollBy(dScrollX / 2, 0)
            }
        }
    }

    /**
     * 复位自身的滚动，分发复位事件，利用 scroller 平滑复位
     */
    private fun resetScroll() {
        // 滚动区域已知才去分发
        if (currRegion != REGION_NONE) {
            listeners.forEach {
                it.onReset(currRegion, getScrollPercent())
            }
            currRegion = REGION_NONE
        }
        scroller.startScroll(scrollX, scrollY, -scrollX, -scrollY)
        invalidate()
    }

    /**
     * 计算并滚到需要滚动到的位置
     */
    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            invalidate()
        }
    }

    /**
     * 根据滚动区域，计算当前区域露出的百分比
     */
    private fun getScrollPercent(): Float {
        return when (currRegion) {
            REGION_TOP -> if (minScrollY != 0) { scrollY * 100F / minScrollY } else { 0F }
            REGION_BOTTOM -> if (maxScrollY != 0) { scrollY * 100F / maxScrollY } else { 0F }
            REGION_LEFT -> if (minScrollX != 0) { scrollX * 100F / minScrollX } else { 0F }
            REGION_RIGHT -> if (maxScrollX != 0) { scrollX * 100F / maxScrollX } else { 0F }
            else -> 0F
        }
    }

    /**
     * 只处理 touch 相关的滚动
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return type == ViewCompat.TYPE_TOUCH
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        parentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    /**
     * 根据滚动区域和新的滚动量确定是否消耗 target 的滚动，滚动区域和处理优先级关系：
     * [REGION_TOP] -> 向下滚动（子 view 向上移动）时优先处理自己，向上滚动时优先处理 target
     * [REGION_BOTTOM] -> 向下滚动时优先处理 target，向上滚动时优先处理自己
     * [REGION_LEFT] -> 向右滚动（子 view 向左移动）时优先处理自己，向左滚动时优先处理 target
     * [REGION_RIGHT] -> 向右滚动时优先处理 target，向左滚动时优先处理自己
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        when (currRegion) {
            REGION_TOP -> if ((dy > 0 && canScrollVertically(dy))
                || (dy < 0 && !target.canScrollVertically(dy))) {
                consumed[1] = dy
            }
            REGION_BOTTOM -> if ((dy > 0 && !target.canScrollVertically(dy))
                || (dy < 0 && canScrollVertically(dy))) {
                consumed[1] = dy
            }
            REGION_LEFT -> if ((dx > 0 && canScrollHorizontally(dx))
                || (dx < 0 && !target.canScrollHorizontally(dx))) {
                consumed[0] = dx
            }
            REGION_RIGHT -> if ((dx > 0 && !target.canScrollHorizontally(dx))
                || (dx < 0 && canScrollHorizontally(dx))) {
                consumed[0] = dx
            }
        }

        dispatchScroll(consumed[0], consumed[1])
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        dispatchScroll(dxUnconsumed, dyUnconsumed)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        parentHelper.onStopNestedScroll(target, type)
    }

    interface Listener {
        /**
         * 滚动发生变化时的回调
         * @param region 当前滚动所在的区域
         * @param percent 当前区域露出的百分比
         */
        fun onScrollChanged(region: Int, percent: Float) {}

        /**
         * 复位时的回调
         * @param region 当前滚动所在的区域
         * @param percent 当前区域露出的百分比
         */
        fun onReset(region: Int, percent: Float) {}
    }

    companion object {
        /**
         * 未知区域
         */
        const val REGION_NONE = 0
        /**
         * 顶部的区域
         */
        const val REGION_TOP = 1
        /**
         * 底部的区域
         */
        const val REGION_BOTTOM = 2
        /**
         * 左边的区域
         */
        const val REGION_LEFT = 3
        /**
         * 右边的区域
         */
        const val REGION_RIGHT = 4

        fun Int.constrains(min: Int, max: Int): Int = when {
            this < min -> min
            this > max -> max
            else -> this
        }
    }

}


