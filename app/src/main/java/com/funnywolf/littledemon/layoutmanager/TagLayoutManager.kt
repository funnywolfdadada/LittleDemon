package com.funnywolf.littledemon.layoutmanager

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min


class TagLayoutManager(@AlignContent private val alignContent: Int = AlignContent.CENTER): RecyclerView.LayoutManager() {

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
                    removeAndRecycleView(child, recycler)
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
        if (childCount == 0 || state.isPreLayout || state.isMeasuring) { return 0 }
        val real: Int
        when {
            // 向上滑动
            dy > 0 -> {
                // 对底部进行布局，并考虑边界
                layoutBottomWhenScrollUp(dy, recycler, state)
                // 判断可以真正滑动的距离，进行滚动
                real = min(dy, getChildBottom() - verticalHelper.endAfterPadding)
                offsetChildrenVertical(-real)
                // 回收顶部不可见的 View
//                recyclerTopWhenScrollUp(real, recycler, state)
            }
            // 向下滑动
            dy < 0 -> {
                // 对顶部进行布局，并考虑边界
//                layoutTopWhenScrollDown(dy, recycler, state)
                // 判断可以真正滑动的距离，进行滚动
                real = max(dy, getChildTop() - verticalHelper.startAfterPadding)
                offsetChildrenVertical(-real)
                // 回收底部不可见的 View
                recyclerBottomWhenScrollDown(real, recycler, state)
            }
            else -> real = 0
        }
        Log.d("ZDL", "scrollVerticallyBy: dy=$dy, real=$real")
        checkPosition()
        return real
    }

    private fun layoutTopWhenScrollDown(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        val firstView = getFirstTopView() ?: return
        val firstViewTop = verticalHelper.getDecoratedStart(firstView)
        val verticalLimit = verticalHelper.startAfterPadding
        // 最后一个 view 还够滑，还不用布局，直接退出
        if (firstViewTop - dy <= verticalLimit) {
            return
        }
        val position = getPosition(firstView)
        // 最后一个 view 就是最后一个 item，不需要再布局，就直接退出
        if (position == 0) {
            return
        }
        // 从最后一个 view 的 bottom 开始 layout 下一个 position 的 view，直到下边界加上滑动后的距离
        // 开始位置下标
        val positionStart = position - 1
        // 结束位置下标
        val positionEnd = 0
        // 水平方向开始坐标
        val horizontalStart = horizontalHelper.startAfterPadding
        // 水平方向结束坐标
        val horizontalEnd = horizontalHelper.endAfterPadding
        // 水平方向偏移
        var horizontalOffset = horizontalStart
        // 垂直方向结束坐标
        val verticalEnd = verticalHelper.startAfterPadding
        // 垂直方向偏移
        var verticalOffset = firstViewTop
        // 最大行高
        var maxLineHeight = 0
        val chunks = ArrayList<ChildChunk>()
        for (i in positionStart downTo positionEnd) {
            val child = recycler.getViewForPosition(i)
            // 测量
            measureChildWithMargins(child, 0, 0)
            val childTotalWidth = horizontalHelper.getDecoratedMeasurement(child)
            val childTotalHeight = verticalHelper.getDecoratedMeasurement(child)
            // 判断下水平方向边界，当前行排列不下时就换行
            if (horizontalOffset + childTotalWidth > horizontalEnd) {
                // verticalOffset 指向的是 view 的底部，这里要加上整行的行高
                chunks.forEach {
                    it.top -= maxLineHeight
                }
                // 换行时把上一行的都 layout 出来
                layoutChunksAndClear(chunks, false)
                if (verticalOffset - maxLineHeight > verticalEnd) {
                    // 达到下边界了，把刚拿出来的 child 回收，退出
                    removeAndRecycleView(child, recycler)
                    break
                } else {
                    // 还有距离就调整偏移量继续 layout
                    horizontalOffset = horizontalStart
                    verticalOffset -= maxLineHeight
                    maxLineHeight = 0
                }
            }
            // 先把布局信息存起来，之后再根据对齐方式 layout
            chunks.add(ChildChunk(child, horizontalOffset, verticalOffset, childTotalWidth, childTotalHeight))
            horizontalOffset += childTotalWidth
            maxLineHeight = max(maxLineHeight, childTotalHeight)
        }
        chunks.forEach {
            it.top -= maxLineHeight
        }
        // layout 最后一行的 view
        layoutChunksAndClear(chunks, false)
    }

    private fun layoutBottomWhenScrollUp(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        val lastView = getLastBottomView() ?: return
        val lastViewBottom = verticalHelper.getDecoratedEnd(lastView)
        val verticalLimit = verticalHelper.endAfterPadding
        // 最后一个 view 还够滑，还不用布局，直接退出
        if (lastViewBottom - dy >= verticalLimit) {
            return
        }
        val position = getPosition(lastView)
        // 最后一个 view 就是最后一个 item，不需要再布局，就直接退出
        if (position == itemCount - 1) {
            return
        }
        // 从最后一个 view 的 bottom 开始 layout 下一个 position 的 view，直到下边界加上滑动后的距离
        // 开始位置下标
        val positionStart = position + 1
        // 结束位置下标
        val positionEnd = itemCount - 1
        // 水平方向开始坐标
        val horizontalStart = horizontalHelper.startAfterPadding
        // 水平方向结束坐标
        val horizontalEnd = horizontalHelper.endAfterPadding
        // 水平方向偏移
        var horizontalOffset = horizontalStart
        // 垂直方向结束坐标
        val verticalEnd = verticalHelper.endAfterPadding
        // 垂直方向偏移
        var verticalOffset = lastViewBottom
        // 最大行高
        var maxLineHeight = 0
        val chunks = ArrayList<ChildChunk>()
        for (i in positionStart..positionEnd) {
            val child = recycler.getViewForPosition(i)
            // 测量
            measureChildWithMargins(child, 0, 0)
            val childTotalWidth = horizontalHelper.getDecoratedMeasurement(child)
            val childTotalHeight = verticalHelper.getDecoratedMeasurement(child)
            // 判断下水平方向边界，当前行排列不下时就换行
            if (horizontalOffset + childTotalWidth > horizontalEnd) {
                // 换行时把上一行的都 layout 出来
                layoutChunksAndClear(chunks, true)
                if (verticalOffset + maxLineHeight > verticalEnd) {
                    // 达到下边界了，把刚拿出来的 child 回收，退出
                    removeAndRecycleView(child, recycler)
                    break
                } else {
                    // 还有距离就调整偏移量继续 layout
                    horizontalOffset = horizontalStart
                    verticalOffset += maxLineHeight
                    maxLineHeight = 0
                }
            }
            // 先把布局信息存起来，之后再根据对齐方式 layout
            chunks.add(ChildChunk(child, horizontalOffset, verticalOffset, childTotalWidth, childTotalHeight))
            horizontalOffset += childTotalWidth
            maxLineHeight = max(maxLineHeight, childTotalHeight)
        }
        // layout 最后一行的 view
        layoutChunksAndClear(chunks, true)
    }

    private fun recyclerTopWhenScrollUp(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            if (verticalHelper.getDecoratedEnd(child) + verticalHelper.getDecoratedMeasurement(child) - dy < verticalHelper.startAfterPadding) {
                Log.d("ZDL", "layoutWhenScroll: recycler ${getPosition(child)}")
                removeAndRecycleView(child, recycler)
            }
        }
    }

    private fun recyclerBottomWhenScrollDown(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        for (i in (childCount - 1) downTo 0) {
            val child = getChildAt(i) ?: continue
            if (verticalHelper.getDecoratedStart(child) - verticalHelper.getDecoratedMeasurement(child) - dy > verticalHelper.endAfterPadding) {
                Log.d("ZDL", "layoutWhenScroll: recycler ${getPosition(child)}")
                removeAndRecycleView(child, recycler)
            }
        }
    }

    private fun layoutChunksAndClear(chunks: ArrayList<ChildChunk>, toBottom: Boolean = true) {
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
            if (toBottom) {
                addView(it.child)
            } else {
                addView(it.child, 0)
            }
            Log.d("ZDL", "layoutWhenScroll: layout ${getPosition(it.child)}")
            layoutDecoratedWithMargins(it.child, it.left, it.top, it.left + it.totalWidth, it.top + it.totalHeight)
        }
        chunks.clear()
    }

    /**
     * 获得最上面的第一个 view
     */
    private fun getFirstTopView(): View? {
        var child = getChildAt(0) ?: return null
        var top = verticalHelper.getDecoratedStart(child)
        var left = horizontalHelper.getDecoratedStart(child)
        for (i in 1 until childCount) {
            val current = getChildAt(i) ?: continue
            val currentTop = verticalHelper.getDecoratedStart(current)
            val currentLeft = horizontalHelper.getDecoratedStart(current)
            if (currentTop < top || (currentTop == top && currentLeft < left)) {
                child = current
                top = currentTop
                left = currentLeft
            }
        }
//        return child
        return getChildAt(0)
    }

    /**
     * 获得最下面的最后一个 view
     */
    private fun getLastBottomView(): View? {
        var child = getChildAt(0) ?: return null
        var bottom = verticalHelper.getDecoratedEnd(child)
        var right = horizontalHelper.getDecoratedEnd(child)
        for (i in 1 until childCount) {
            val current = getChildAt(i) ?: continue
            val currentBottom = verticalHelper.getDecoratedEnd(current)
            val currentRight = horizontalHelper.getDecoratedEnd(current)
            if (currentBottom > bottom || (currentBottom == bottom && currentRight > right)) {
                child = current
                bottom = currentBottom
                right = currentRight
            }
        }
//        return child
        return getChildAt(childCount - 1)
    }

    private fun getChildTop(): Int {
        return verticalHelper.getDecoratedStart(getFirstTopView() ?: return 0)
    }

    private fun getChildBottom(): Int {
        return verticalHelper.getDecoratedEnd(getLastBottomView() ?: return 0)
    }

    private fun checkPosition() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val position = if (child != null) {
                getPosition(child)
            }else {
                -1
            }
            if (position != i) {
                Log.d("ZDL", "checkPosition: child at $i, position=$position")
            }
        }
    }

}
