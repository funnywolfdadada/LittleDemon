package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.R
import kotlinx.android.synthetic.main.fragment_layout_drag_close.view.*

class DragCloseFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_drag_close, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.dragCloseLayout.setOnCloseListener(this::onDragClose)
        view.dragCloseLayout.setCanScrollView(view.scrollView)
    }

    private fun onDragClose(): Boolean {
        val manager = fragmentManager ?: return false
        manager
            .beginTransaction()
            .remove(this)
            .commit()
        return true
    }
}