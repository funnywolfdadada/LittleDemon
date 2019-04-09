package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.utils.getScreenHeight
import kotlinx.android.synthetic.main.fragment_layout_nest_scroll_view.*
import kotlinx.android.synthetic.main.fragment_layout_scroll_and_extend.*

/**
 * @author zhaodongliang @ Zhihu Inc.
 * @since 2019/4/9
 */
class ScrollAndDragExtentFragment: Fragment() {
    private var contentView: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_scroll_and_extend, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layout.setOnClickListener {
            Toast.makeText(context, "click content", Toast.LENGTH_SHORT).show()
            val v = layout.findViewById<View>(R.id.footerContent)
            if (v != null) {
                layout.removeView(v)
                dragExtendLayout.addView(v)
            } else {
                dragExtendLayout.removeView(contentView)
                layout.addView(contentView)
            }
        }

        contentView = footerContent

        dragExtendLayout.setExtendHeight(getScreenHeight(context ?: return) / 6.0f)
        fragmentManager
            ?.beginTransaction()
            ?.add(R.id.footerContent, SimpleViewPagerFragment())
            ?.commit()
    }
}