package com.funnywolf.littledemon.utils

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewLoadMore(recyclerView: RecyclerView, var thresholdPixel: Int, var thresholdCount: Int, private val loadMore: () -> Unit)
    : RecyclerView.OnScrollListener(), View.OnLayoutChangeListener {

    private var newScroll = true

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
            newScroll = true
        }
        if (newScroll) {
            tryLoadMore(recyclerView)
        }
    }

    private fun tryLoadMore(v: View?) {
        Log.d("ZDL", "tryLoadMore")
        val recyclerView = (v as? RecyclerView) ?: return
        val layoutManager = recyclerView.layoutManager
        if ((layoutManager is LinearLayoutManager && layoutManager.itemCount > 0
                    && layoutManager.itemCount - 1 - layoutManager.findLastVisibleItemPosition() < thresholdCount)
            || !recyclerView.canScrollVertically(thresholdPixel)) {
            invokeLoadMore()
        }
    }

    private fun invokeLoadMore() {
        Log.d("ZDL", "invokeLoadMore")
        newScroll = false
        loadMore.invoke()
    }

    companion object {
        fun bind(recyclerView: RecyclerView, thresholdPixel: Int = 1, thresholdCount: Int = 5, loadMore: () -> Unit) {
            RecyclerViewLoadMore(recyclerView, thresholdPixel, thresholdCount, loadMore)
        }
    }
}