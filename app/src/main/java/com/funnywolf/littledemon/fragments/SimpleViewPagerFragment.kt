package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import com.funnywolf.littledemon.R
import kotlinx.android.synthetic.main.fragment_layout_simple_view_pager.view.*

class SimpleViewPagerFragment: Fragment() {

    private val images = arrayOf(R.mipmap.bg0, R.mipmap.bg1, R.mipmap.bg2)
    private val imageViews = ArrayList<ImageView>(3)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_simple_view_pager, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        images.forEach {
            val imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageView.setImageResource(it)
            imageViews.add(imageView)
        }
        view.viewPager.adapter = object: PagerAdapter() {
            override fun isViewFromObject(p0: View, p1: Any): Boolean {
                return p0 === p1
            }

            override fun getCount() = imageViews.size

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                container.addView(imageViews[position])
                return imageViews[position]
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(imageViews[position])
            }
        }
    }
}