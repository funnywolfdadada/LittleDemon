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
        MainActivity.startFragment(TestFragment::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        bind(testFragment, TestFragment::class.java)
        bind(simpleList, SimpleListFragment::class.java)
        bind(fragmentViewPager, FragmentViewPagerFragment::class.java)
        bind(motionLayout, MotionLayoutTestFragment::class.java)
        bind(coordinatorLayout, CoordinatorLayoutTestFragment::class.java)
        bind(viewDragTest, ViewDragHelperTestFragment::class.java)
    }

    private fun bind(v: View, fragmentClass: Class<out Fragment>) {
        v.setOnClickListener {
            MainActivity.startFragment(fragmentClass)
        }
    }

}
