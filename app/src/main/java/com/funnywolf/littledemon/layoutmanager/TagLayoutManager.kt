package com.funnywolf.littledemon.layoutmanager

import android.util.Log
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max


class TagLayoutManager(@AlignContent private val alignContent: Int = AlignContent.CENTER_SPREAD): RecyclerView.LayoutManager() {

    private val horizontalHelper = OrientationHelper.createHorizontalHelper(this)
    private val verticalHelper = OrientationHelper.createVerticalHelper(this)

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
        // 填充 View
        fill(recycler)
        // 滚到之前的位置
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        // 上侧偏移
        var topOffset = getTopOffset()
        // 左侧偏移
        var leftOffset = horizontalHelper.startAfterPadding
        // 每行最大高度
        var lineMaxHeight = 0
        // onLayoutChildren 会调用两次，都需要先把之前 attach 的 detach 掉，然后再利用这些缓存
        detachAndScrapAttachedViews(recycler)
        val chunks = ArrayList<ChildChunk>()
        // 初始化时不知道要布多少个 view，假设从 0 到 itemCount - 1
        for (i in 0 until itemCount) {
            val child = recycler.getViewForPosition(i)
            addView(child)
            // 测量
            measureChildWithMargins(child, 0, 0)
            val childTotalWidth = horizontalHelper.getDecoratedMeasurement(child)
            val childTotalHeight = verticalHelper.getDecoratedMeasurement(child)
            // 判断下水平方向边界，当前行排列不下时就换行
            if (leftOffset + childTotalWidth > horizontalHelper.endAfterPadding) {
                // 换行时把上一行的都 layout 出来
                layoutChunksAndClear(chunks)
                topOffset += lineMaxHeight
                if (topOffset > verticalHelper.end) {
                    break
                }
                leftOffset = horizontalHelper.startAfterPadding
                lineMaxHeight = 0
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
            layoutDecoratedWithMargins(it.child, it.left, it.top, it.left + it.totalWidth, it.top + it.totalHeight)
        }
        chunks.clear()
    }

    private fun getTopOffset(): Int {
        val first = getChildAt(0)
        // 还没有 view 或者
        return if (first == null || getPosition(first) != 0) {
            verticalHelper.startAfterPadding
        } else {
            verticalHelper.getDecoratedStart(first)
        }
    }

    override fun canScrollVertically(): Boolean = true

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (dy == 0 || childCount == 0 || state.isPreLayout || state.isMeasuring) { return 0 }
        // 根据滚动进行布局和回收，并考虑边界，返回真正滚动的距离
        val real = layoutWhenScroll(dy, recycler, state)
        // 滚动
        if (real != 0) {
            offsetChildrenVertical(-real)
        }
        return real
    }

    private fun layoutWhenScroll(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        // 根据滚动方向布局滑进来的 view
        val real = when {
            dy > 0 -> layoutBottomWhenScrollUp(dy, recycler, state)
            dy < 0 -> layoutTopWhenScrollDown(-dy, recycler, state)
            else -> 0
        }
        // 回收不可见的 View
//        for (i in 0 until childCount) {
//            val child = getChildAt(i) ?: continue
//            if ((real < 0 && verticalHelper.getDecoratedStart(child) - real > verticalHelper.endAfterPadding)
//                || (real > 0 && verticalHelper.getDecoratedEnd(child) - real < 0)) {
//                removeAndRecycleView(child, recycler)
//            }
//        }
        return real
    }

    private fun layoutTopWhenScrollDown(dyAbs: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val firstChild = getChildAt(0) ?: return 0
        val childTop = verticalHelper.getDecoratedStart(firstChild)
        var topOffset = childTop + dyAbs
        // 第一个 child 还没滑到头，直接返回滑动距离
        if (topOffset < 0) {
            return -dyAbs
        }
        val position = getPosition(firstChild)
        when (position) {
            RecyclerView.NO_POSITION -> return 0
            // 第一个 view 是第一个 item，只用计算下偏移量
            0 -> return childTop
        }
        return -dyAbs
    }

    private fun layoutBottomWhenScrollUp(dyAbs: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val lastChild = getChildAt(0) ?: return 0
        val childBottom = verticalHelper.getDecoratedEnd(lastChild)
        var bottomOffset = childBottom + dyAbs
        // 最后一个 child 还没滑到头，直接返回滑动距离
        if (bottomOffset < 0) {
            return -dyAbs
        }
        val position = getPosition(lastChild)
        when (position) {
            RecyclerView.NO_POSITION -> return 0
            // 第一个 view 是第一个 item，只用计算下偏移量
            childCount - 1 -> return childBottom
        }
        return dyAbs
    }

}
