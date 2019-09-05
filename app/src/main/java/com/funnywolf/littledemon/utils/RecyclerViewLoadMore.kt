package com.funnywolf.littledemon.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewLoadMore(recyclerView: RecyclerView, private val loadMore: () -> Unit)
    : RecyclerView.OnScrollListener(), View.OnLayoutChangeListener {

    init {
        recyclerView.addOnScrollListener(this)
        recyclerView.addOnLayoutChangeListener(this)
    }

    override fun onLayoutChange(v: View?,
        left: Int, top: Int, right: Int, bottom: Int,
        oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
        if (v?.canScrollVertically(1) == false) {
            loadMore.invoke()
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
            loadMore.invoke()
        }

    }


    companion object {
        fun bind(recyclerView: RecyclerView, loadMore: () -> Unit) {
            RecyclerViewLoadMore(recyclerView, loadMore)
        }
    }
}