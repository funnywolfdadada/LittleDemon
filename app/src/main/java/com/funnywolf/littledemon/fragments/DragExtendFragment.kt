package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.funnywolf.littledemon.R
import kotlinx.android.synthetic.main.fragment_layout_drag_extent.*
import kotlinx.android.synthetic.main.layout_drag_extend.view.*

class DragExtendFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_drag_extent, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dragExtend.bottomLayout.setOnClickListener {
            onClose()
        }
    }

    private fun onClose(): Boolean {
        val manager = fragmentManager ?: return false
        manager
            .beginTransaction()
            .remove(this)
            .commit()
        return true
    }
}