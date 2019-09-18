package com.funnywolf.littledemon.layoutmanager

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max


class TagLayoutManagerWithoutRecycler(@AlignContent private val alignContent: Int = AlignContent.CENTER): RecyclerView.LayoutManager() {

    private val horizontalHelper = OrientationHelper.createHorizontalHelper(this)
    private val verticalHelper = OrientationHelper.createVerticalHelper(this)
    private var topmostChild: View? = null
    private var bottommostChild: View? = null

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        Log.d("ZDL", "onLayoutChildren itemCount = $itemCount, childCount = $childCount")
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            return
        }
        if (childCount == 0 && state.isPreLayout) {
            return
        }
        // 记录当前偏移
        val verticalOffset = currentVisibleTop()
        // 填充 View
        fill(recycler)
        // 滚到之前的位置
        scrollVerticallyBy(-verticalOffset, recycler, state)
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        // onLayoutChildren 会调用两次，都需要先把之前 attach 的 detach 掉，然后再利用这些缓存
        detachAndScrapAttachedViews(recycler)
        // 上侧偏移
        var topOffset = verticalHelper.startAfterPadding
        // 左侧偏移
        var leftOffset = horizontalHelper.startAfterPadding
        // 每行最大高度
        var lineMaxHeight = 0
        val chunks = ArrayList<ChildChunk>()
        // 初始化时不知道要布多少个 view，假设从 0 到 itemCount - 1
        for (i in 0 until itemCount) {
            val child = recycler.getViewForPosition(i)
            // 测量
            measureChildWithMargins(child, 0, 0)
            val childTotalWidth = horizontalHelper.getDecoratedMeasurement(child)
            val childTotalHeight = verticalHelper.getDecoratedMeasurement(child)
            // 判断下水平方向边界，当前行排列不下时就换行
            if (leftOffset + childTotalWidth > horizontalHelper.endAfterPadding) {
                leftOffset = horizontalHelper.startAfterPadding
                topOffset += lineMaxHeight
                lineMaxHeight = 0
                // 换行时把上一行的都 layout 出来
                layoutChunksAndClear(chunks)
            }
            // 先把布局信息存起来，之后再根据对齐方式 layout
            chunks.add(ChildChunk(child, leftOffset, topOffset, childTotalWidth, childTotalHeight))
            leftOffset += childTotalWidth
            lineMaxHeight = max(lineMaxHeight, childTotalHeight)
        }
        // layout 最后一行的 view
        layoutChunksAndClear(chunks)
    }

    private fun layoutChunksAndClear(chunks: ArrayList<ChildChunk>) {
        if (chunks.size == 0) { return }
        val totalBlankSpace = horizontalHelper.totalSpace - chunks.sumBy { it.totalWidth }
        // 根据不同的对其方式调整 left
        when (alignContent) {
            AlignContent.END -> {
                chunks.forEach { it.left += totalBlankSpace }
            }
            AlignContent.CENTER -> {
                val leftOffset = totalBlankSpace / (chunks.size + 1)
                for (i in 0 until chunks.size) {
                    chunks[i].left += leftOffset * (i + 1)
                }
            }
            AlignContent.CENTER_PACKED -> {
                val leftOffset = totalBlankSpace / 2
                chunks.forEach { it.left += leftOffset }
            }
            AlignContent.CENTER_SPREAD -> {
                if (chunks.size > 1) {
                    val leftOffset = totalBlankSpace / (chunks.size - 1)
                    for (i in 1 until chunks.size) {
                        chunks[i].left += leftOffset * i
                    }
                }
            }
        }
        // layout
        chunks.forEach {
            addView(it.child)
            layoutDecoratedWithMargins(it.child, it.left, it.top, it.left + it.totalWidth, it.top + it.totalHeight)
            topmostChild = topmostChild?.run {
                if (it.top < verticalHelper.getDecoratedStart(this)) {
                    it.child
                } else {
                    topmostChild
                }
            } ?: it.child
            bottommostChild = bottommostChild?.run {
                if (it.top + it.totalHeight > verticalHelper.getDecoratedEnd(this)) {
                    it.child
                } else {
                    bottommostChild
                }
            } ?: it.child
        }
        chunks.clear()
    }

    override fun canScrollVertically(): Boolean = true

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (dy == 0 || childCount == 0 || state.isPreLayout || state.isMeasuring) { return 0 }
        // 根据边界计算真正需要滚动的距离
        val top = currentVisibleTop()
        val bottom = currentVisibleBottom()
        val topLimit = verticalHelper.startAfterPadding
        val bottomLimit = verticalHelper.endAfterPadding
        val real = when {
            // 不足一页
            bottomLimit - topLimit > bottom - top -> 0
            // 上边界
            dy < 0 && top - dy > topLimit -> top - topLimit
            // 下边界
            dy > 0 && bottom - dy < bottomLimit -> bottom - bottomLimit
            else -> dy
        }
        // 滚动
        if (real != 0) {
            offsetChildrenVertical(-real)
        }
        return real
    }

    /**
     * 当前所有可见 View 的 top
     */
    private fun currentVisibleTop(): Int {
        return topmostChild?.run {
            verticalHelper.getDecoratedStart(this)
        } ?: 0
    }

    /**
     * 当前所有可见 View 的 bottom
     */
    private fun currentVisibleBottom(): Int {
        return bottommostChild?.run {
            verticalHelper.getDecoratedEnd(this)
        } ?: 0
    }

}
