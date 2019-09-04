package com.funnywolf.littledemon.fragments

import android.animation.FloatEvaluator
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.utils.getScreenHeight
import com.google.android.material.animation.ArgbEvaluatorCompat
import kotlinx.android.synthetic.main.fragment_layout_fragment_view_pager.*

class FragmentViewPagerFragment: Fragment(), ViewPager.OnPageChangeListener {
    private lateinit var tabs: Array<TextView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_fragment_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.layoutParams.height = getScreenHeight(context ?: return)
        viewPager.adapter = PagerAdapter(listOf(SimpleListFragment(), SimpleListFragment(), SimpleListFragment()),
            childFragmentManager)
        viewPager.addOnPageChangeListener(this)
        tabs = arrayOf(tab1, tab2, tab3)
        repeat(tabs.count()) { index ->
            tabs[index].setOnClickListener {
                viewPager.currentItem = index
            }
            tabs[index].alpha = if (index == viewPager.currentItem) {
                getAlphaByProgress(1F)
            } else {
                getAlphaByProgress(0F)
            }
            tabs[index].setTextColor(if (index == viewPager.currentItem) {
                getColorByProgress(1F)
            } else {
                getColorByProgress(0F)
            })
        }
        onPageSelected(viewPager.currentItem)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        Log.d("ZDL", "position = ${position + positionOffset}")
        tabs[position].alpha = getAlphaByProgress(1 - positionOffset)
        tabs[position].setTextColor(getColorByProgress(1 - positionOffset))
        if (position < tabs.size - 1) {
            tabs[position + 1].alpha = getAlphaByProgress(positionOffset)
            tabs[position + 1].setTextColor(getColorByProgress(positionOffset))
        }
    }

    override fun onPageSelected(position: Int) {
    }

    private val floatEvaluator = FloatEvaluator()
    private fun getAlphaByProgress(progress: Float, minAlpha: Float = 0.5F, maxAlpha: Float = 1F): Float {
        return floatEvaluator.evaluate(progress, minAlpha, maxAlpha)
    }

    private fun getColorByProgress(progress: Float, startColor: Int = Color.WHITE, endColor: Int = Color.GREEN): Int {
        return ArgbEvaluatorCompat.getInstance().evaluate(progress, startColor, endColor)
    }
}

class PagerAdapter(private val list: List<Fragment>, fm: FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) = list[position]

    override fun getCount() = list.size
}