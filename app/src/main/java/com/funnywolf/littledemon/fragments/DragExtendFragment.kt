package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.utils.*
import kotlinx.android.synthetic.main.fragment_layout_drag_extent.*

class DragExtendFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_drag_extent, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dragExtendLayout.setExtendHeight(getScreenHeight(context ?: return) / 6.0f)
        fragmentManager
            ?.beginTransaction()
            ?.add(R.id.content, NestScrollViewFragment())
            ?.commit()
    }
}