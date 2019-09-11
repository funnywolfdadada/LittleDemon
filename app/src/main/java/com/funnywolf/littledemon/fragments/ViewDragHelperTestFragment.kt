package com.funnywolf.littledemon.fragments

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.simpleadapter.SimpleAdapter
import com.funnywolf.littledemon.utils.constrain
import com.funnywolf.littledemon.utils.createSimpleStringHolderInfo
import com.funnywolf.littledemon.utils.getRandomStrings
import kotlinx.android.synthetic.main.fragment_layout_view_drag_helper_test.*
import java.lang.ref.WeakReference

class ViewDragHelperTestFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layout_view_drag_helper_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SimpleDragBehavior.setup(dragMe)
        SimpleBottomSheetBehavior.setup(linearLayout)
        recyclerView.adapter = SimpleAdapter.Builder(getRandomStrings(50))
            .add(createSimpleStringHolderInfo())
            .build()
    }

}

class SimpleDragBehavior: CoordinatorLayout.Behavior<View> {
    var dragViewId: Int = 0
    private var dragHelper: ViewDragHelper? = null
    private var parentRef: WeakReference<CoordinatorLayout>? = null

    private val dragCallback = object: ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child.id == dragViewId
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            val parent = parentRef?.get() ?: return left
            return constrain(left, 0, parent.width - child.width)
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val parent = parentRef?.get() ?: return top
            return constrain(top, 0, parent.height - child.height)
        }

    }

    constructor(): super()
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        parentRef = WeakReference(parent)
        dragHelper = ViewDragHelper.create(parent, dragCallback)
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
        super.onAttachedToLayoutParams(params)
        dragHelper = null
        parentRef = null
    }

    override fun onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams()
        dragHelper = null
        parentRef = null
    }

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: View,
        ev: MotionEvent
    ): Boolean {
        return ev.x in child.x..(child.x + child.width) && ev.y in child.y..(child.y + child.height)
    }

    override fun onTouchEvent(
        parent: CoordinatorLayout,
        child: View,
        ev: MotionEvent
    ): Boolean {
        return if (dragHelper == null) {
            false
        } else {
            dragHelper?.processTouchEvent(ev)
            true
        }
    }

    companion object {
        fun setup(v: View) {
            val behavior = SimpleDragBehavior()
            behavior.dragViewId = v.id
            (v.layoutParams as CoordinatorLayout.LayoutParams).behavior = behavior
        }
    }

}

class SimpleBottomSheetBehavior : CoordinatorLayout.Behavior<View> {
    var dragViewId: Int = 0
    private var dragHelper: ViewDragHelper? = null
    private var parentRef: WeakReference<CoordinatorLayout>? = null
    private var scrollableChildRef: WeakReference<View>? = null
    private var childMinTop = 0

    /**
     * for [onInterceptTouchEvent]
     */
    private var lastY: Float = 0F

    private val dragCallback = object: ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child.id == dragViewId
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return child.height - 300
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return constrain(top, childMinTop, child.height - 300)
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            Log.d("ZDL", "onViewPositionChanged: ${(changedView.top - childMinTop).toFloat() / (changedView.height - 300 - childMinTop)}")
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            if (releasedChild.id != dragViewId) { return }
            val expandTop = childMinTop
            val collapseTop = releasedChild.height - releasedChild.minimumHeight
            val finalTop = when {
                yvel >= 0 -> collapseTop
                releasedChild.top > collapseTop -> collapseTop
                else -> expandTop
            }
            if (dragHelper?.smoothSlideViewTo(releasedChild, releasedChild.left, finalTop) == true) {
                ViewCompat.postOnAnimation(releasedChild, SettleRunnable(releasedChild))
            }
        }
    }

    constructor(): super()
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        parentRef = WeakReference(parent)
        dragHelper = ViewDragHelper.create(parent, dragCallback)
        val scrollableChild = findScrollableChild(child)
        if (scrollableChild != null) {
            scrollableChildRef = WeakReference(scrollableChild)
        }
        return false
    }

    override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
        super.onAttachedToLayoutParams(params)
        dragHelper = null
        parentRef = null
    }

    override fun onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams()
        dragHelper = null
        parentRef = null
    }

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: View,
        ev: MotionEvent
    ): Boolean {
        // 如果未全部展开，则拦截
        if (child.top > childMinTop) {
            return true
        }
        // 如果点击位置的找不到可滚动的 View，则拦截
        val scrollableChild = getTouchedScrollableChild(parent, ev) ?: return true
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> lastY = ev.y
            MotionEvent.ACTION_MOVE -> {
                val dy = ev.y - lastY
                lastY = ev.y
                // 此时点击位置的 View 可滑动
                // 只有在向下滑，且滑不动的情况下才拦截
                return dy > 0 && !scrollableChild.canScrollVertically(-dy.toInt())
            }
        }
        // 其他情况根据 ViewDragHelper 判断是否拦截
        return dragHelper?.shouldInterceptTouchEvent(ev) ?: false
    }

    override fun onTouchEvent(
        parent: CoordinatorLayout,
        child: View,
        ev: MotionEvent
    ): Boolean {
        return if (dragHelper == null) {
            false
        } else {
            dragHelper?.processTouchEvent(ev)
            true
        }
    }

    private fun getTouchedScrollableChild(parent: CoordinatorLayout, ev: MotionEvent): View? {
        val scrollableChild = scrollableChildRef?.get()
        if (scrollableChild != null
            && parent.isPointInChildBounds(scrollableChild, ev.x.toInt(), ev.y.toInt())) {
            return scrollableChild
        }
        return null
    }

    private fun findScrollableChild(view: View): View? {
        if (ViewCompat.isNestedScrollingEnabled(view)) {
            return view
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val scrollableChild = findScrollableChild(view.getChildAt(i))
                if (scrollableChild != null) {
                    return scrollableChild
                }
            }
        }
        return null
    }

    private inner class SettleRunnable(val view: View): Runnable {
        override fun run() {
            if (dragHelper?.continueSettling(true) == true) {
                ViewCompat.postOnAnimation(view, this)
            }
        }
    }

    companion object {
        fun setup(v: View) {
            val behavior = SimpleBottomSheetBehavior()
            behavior.dragViewId = v.id
            (v.layoutParams as CoordinatorLayout.LayoutParams).behavior = behavior
        }
    }

}
