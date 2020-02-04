package com.funnywolf.littledemon.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 根据 [RecyclerView] 的垂直可滑动距离 [thresholdPixel]，触发 [loadMore]
 *
 * @author funnywolf
 * @since 2020/02/04
 */
class RecyclerViewLoadMore(
    recyclerView: RecyclerView,
    private val thresholdPixel: Int,
    private val loadMore: () -> Unit)
    : RecyclerView.OnScrollListener(), View.OnLayoutChangeListener {

    init {
        recyclerView.addOnScrollListener(this)
        recyclerView.addOnLayoutChangeListener(this)
    }

    override fun onLayoutChange(v: View?,
        left: Int, top: Int, right: Int, bottom: Int,
        oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
        tryLoadMore(v)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            tryLoadMore(recyclerView)
        }
    }

    private fun tryLoadMore(v: View?) {
        if (v?.canScrollVertically(thresholdPixel) == false) {
            loadMore.invoke()
        }
    }

}