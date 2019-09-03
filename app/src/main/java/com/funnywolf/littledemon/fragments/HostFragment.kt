package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.MainActivity
import com.funnywolf.littledemon.R
import kotlinx.android.synthetic.main.fragment_layout_host.*

class HostFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.startFragment(MotionLayoutTestFragment::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        bind(dragClose, DragCloseFragment::class.java)
        bind(dragExtend, DragExtendFragment::class.java)
        bind(nestScrollView, NestScrollViewFragment::class.java)
        bind(constrainLayoutTest, ConstraintLayoutTestFragment::class.java)
        bind(scrollAndDragExtend, ScrollAndDragExtentFragment::class.java)
        bind(simpleList, SimpleListFragment::class.java)
        bind(fragmentViewPager, FragmentViewPagerFragment::class.java)
        bind(layoutTest, LayoutTestFragment::class.java)
        bind(foldedText, FoldedTextFragment::class.java)
        bind(drawerList, DrawerListFragment::class.java)
        bind(popupWindowTest, PopupWindowTestFragment::class.java)
        bind(motionLayout, MotionLayoutTestFragment::class.java)
    }

    private fun bind(v: View, fragmentClass: Class<out Fragment>) {
        v.setOnClickListener {
            MainActivity.startFragment(fragmentClass)
        }
    }

}
