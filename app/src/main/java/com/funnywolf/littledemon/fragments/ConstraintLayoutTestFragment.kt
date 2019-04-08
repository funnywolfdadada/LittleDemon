package com.funnywolf.littledemon.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.utils.dp2pix
import kotlinx.android.synthetic.main.fragment_layout_constraint_test.*

/**
 * @author zhaodongliang @ Zhihu Inc.
 * @since 2019/3/22
 */
class ConstraintLayoutTestFragment: Fragment() {
    val N = 7
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_constraint_test, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dAngle = 360.0f / N
        val dp100 = dp2pix(context ?: return, 100)
        for (i in 0..N) {
            val v = ImageView(context ?: return)
            v.setImageResource(R.drawable.ic_sun)
            v.setBackgroundResource(R.drawable.green_circle_background)
            v.imageTintList = ColorStateList.valueOf(Color.YELLOW)
            val parmas = ConstraintLayout.LayoutParams(dp100 / 2, dp100 / 2)
            parmas.circleConstraint = R.id.green_man
            parmas.circleAngle = i * dAngle
            parmas.circleRadius = dp100
            v.layoutParams = parmas
            contentLayout.addView(v)
            v.rotation = i * dAngle
        }
    }
}