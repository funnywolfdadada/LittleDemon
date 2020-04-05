package com.funnywolf.littledemon.scene

import android.animation.FloatEvaluator
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.scene.Scene
import com.funnywolf.hollowkit.recyclerview.SimpleAdapter
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.utils.createSimpleStringHolderInfo
import com.funnywolf.littledemon.utils.getRandomStrings
import java.lang.ref.WeakReference

class CoordinatorLayoutTestScene: Scene() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.scene_layout_coordinator_layout_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        ((recyclerView.layoutParams as? CoordinatorLayout.LayoutParams)
            ?.behavior as? HeaderCollapseBehavior)?.dependencyId = R.id.collapseHeaderImage

        recyclerView.adapter = SimpleAdapter(getRandomStrings(100))
            .addHolderInfo(createSimpleStringHolderInfo())
    }

}

class HeaderCollapseBehavior(context: Context, attributeSet: AttributeSet) :
    CoordinatorLayout.Behavior<RecyclerView>(context, attributeSet) {

    var dependencyId = 0

    private var dependencyRef: WeakReference<View>? = null
    private val floatEvaluator = FloatEvaluator()

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: RecyclerView,
        dependency: View
    ): Boolean {
        if (dependency.id != dependencyId) { return false }
        dependencyRef = WeakReference(dependency)
        return true
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: RecyclerView,
        layoutDirection: Int
    ): Boolean {
        val dependency = dependencyRef?.get() ?: return false
        if (child.layoutParams.height == CoordinatorLayout.LayoutParams.MATCH_PARENT) {
            child.layout(0, dependency.minimumHeight, parent.width, parent.height)
            return true
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: RecyclerView,
        dependency: View
    ): Boolean {
        child.y = dependency.y + dependency.height
        return true
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: RecyclerView,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: RecyclerView,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        // 向下滑，且 RecyclerView 也能向下滑就先不处理
        if (dy < 0 && child.canScrollVertically(dy)) {
            return
        }
        val dependency = dependencyRef?.get() ?: return
        val dependencyMinY = dependency.top.toFloat() - (dependency.height - dependency.minimumHeight)
        val dependencyMaxY = dependency.top.toFloat()
        val newY = dependency.y - dy

        when {
            newY > dependencyMaxY -> {
                consumed[1] = (dependencyMaxY - dependency.y).toInt()
                dependency.y = dependencyMaxY
            }
            newY < dependencyMinY -> {
                consumed[1] = (dependencyMinY - dependency.y).toInt()
                dependency.y = dependencyMinY
            }
            else -> {
                consumed[1] = dy
                dependency.y = newY
            }
        }

        val progress = (dependency.y - dependencyMinY) / (dependencyMaxY - dependencyMinY)
        dependency.alpha = floatEvaluator.evaluate(progress, 0, 1)
        dependency.scaleX = floatEvaluator.evaluate(progress, 1.5, 1)
        dependency.scaleY = floatEvaluator.evaluate(progress, 1.5, 1)
    }

}
