package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.utils.getScreenHeight
import kotlinx.android.synthetic.main.fragment_layout_simple_view_pager.*
import kotlinx.android.synthetic.main.fragment_layout_simple_view_pager.view.*

class SimpleViewPagerFragment: Fragment() {
    companion object {
        const val TAG = "SimpleViewPagerFragment"
    }

    private val images = arrayOf(R.mipmap.bg0, R.mipmap.bg1, R.mipmap.bg2)
    private val imageViews = ArrayList<ImageView>(3)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView: ")
        return inflater.inflate(R.layout.fragment_layout_simple_view_pager, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.setOnClickListener {
            Toast.makeText(context, "click title", Toast.LENGTH_SHORT).show()
        }
        view.layoutParams.height = getScreenHeight(context ?: return)
        view.requestLayout()
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

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }
}