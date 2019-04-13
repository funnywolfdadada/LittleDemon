package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.R
import kotlinx.android.synthetic.main.fragment_layout_nest_scroll_view.*

class NestScrollViewFragment: Fragment() {
    private var contentView: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_nest_scroll_view, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layout.setOnClickListener {
            Toast.makeText(context, "click content", Toast.LENGTH_SHORT).show()
            val v = layout.findViewById<View>(R.id.content)
            if (v != null) {
                layout.removeView(v)
            } else {
                layout.addView(contentView)
            }
        }
        contentView = content
        fragmentManager
            ?.beginTransaction()
            ?.add(R.id.content, FragmentViewPagerFragment())
            ?.commit()
    }

}