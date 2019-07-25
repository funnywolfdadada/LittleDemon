package com.funnywolf.littledemon.fragments

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.R
import kotlinx.android.synthetic.main.fragment_layout_popupwindow_test.*

class PopupWindowTestFragment: Fragment() {

    private val popupWindow: PopupWindow by lazy {
        return@lazy PopupWindow(View.inflate(context, R.layout.popupwindow_layout_test, null),
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(BitmapDrawable())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_popupwindow_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {  }
        show_top.setOnClickListener { showWindow(Gravity.TOP) }
        show_bottom.setOnClickListener { showWindow(Gravity.BOTTOM) }
        show_left.setOnClickListener { showWindow(Gravity.START) }
        show_right.setOnClickListener { showWindow(Gravity.END) }

    }

    private fun showWindow(gravity: Int) {
        popupWindow.dismiss()
        popupWindow.showAsDropDown(anch, 0, 0, gravity)
    }

}