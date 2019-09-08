package com.funnywolf.littledemon.fragments

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
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
        SimpleBottomSheetBehavior.setup(recyclerView)
        recyclerView.adapter = SimpleAdapter.Builder(getRandomStrings(30))
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

    private val dragCallback = object: ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child.id == dragViewId
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val parent = parentRef?.get() ?: return top
            return constrain(top, 0, parent.height)
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            if (releasedChild.id != dragViewId) { return }
            val parent = parentRef?.get() ?: return
            val finalTop = if (yvel > 0) {
                parent.height - releasedChild.minimumHeight
            } else {
                0
            }
            releasedChild.animate().y(finalTop.toFloat()).start()
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

    private var lastY: Float = 0F
    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: View,
        ev: MotionEvent
    ): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = ev.y - lastY
                lastY = ev.y
                if (dy > 0 && !child.canScrollVertically(-dy.toInt())) {
                    ev.action = MotionEvent.ACTION_DOWN
                    dragHelper?.processTouchEvent(ev)
                    return true
                }
            }
        }
        return child.top != 0
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
            val behavior = SimpleBottomSheetBehavior()
            behavior.dragViewId = v.id
            (v.layoutParams as CoordinatorLayout.LayoutParams).behavior = behavior
        }
    }

}
