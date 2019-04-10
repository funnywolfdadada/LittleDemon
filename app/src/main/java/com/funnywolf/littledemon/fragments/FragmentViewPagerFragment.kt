package com.funnywolf.littledemon.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.funnywolf.littledemon.R
import kotlinx.android.synthetic.main.fragment_layout_fragment_view_pager.*

/**
 * @author zhaodongliang @ Zhihu Inc.
 * @since 2019/4/10
 */
class FragmentViewPagerFragment: Fragment(), ViewPager.OnPageChangeListener {
    private var tabs: Array<TextView>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_fragment_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = PagerAdapter(getFragments(), fragmentManager ?: return)
        viewPager.addOnPageChangeListener(this)
        tabs = arrayOf(tab1, tab2, tab3)
        repeat(tabs?.count() ?: return) { index ->
            tabs!![index].setOnClickListener {
                viewPager.currentItem = index
            }
        }
        onPageSelected(viewPager.currentItem)
    }

    private fun getFragments(): List<Fragment> {
        return listOf(SimpleListFragment(), SimpleListFragment(), SimpleListFragment())
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        repeat(tabs?.count() ?: return) {
            if (it == position) {
                tabs!![it].setTextColor(Color.GREEN)
            } else {
                tabs!![it].setTextColor(Color.WHITE)
            }
        }
    }
}

class PagerAdapter(list: List<Fragment>, fm: FragmentManager): FragmentPagerAdapter(fm) {
    private val list = ArrayList<Fragment>()

    init {
        this.list.addAll(list)
    }

    override fun getItem(position: Int) = list[position]

    override fun getCount() = list.size
}