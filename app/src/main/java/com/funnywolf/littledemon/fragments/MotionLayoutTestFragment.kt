package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.R
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_layout_motion_layout_with_coordiantor_test.*

class MotionLayoutTestFragment: Fragment() {

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_layout_motion_layout_test, container, false)
//    }

    /******************************************************************************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_motion_layout_with_coordiantor_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            motionLayout.progress = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
        })
    }

}
