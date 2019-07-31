package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.R
import kotlinx.android.synthetic.main.test_layout.*

class LayoutTestFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.test_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            val time = System.nanoTime()
            repeat(1) {
                top.visibility = View.GONE
                top.visibility = View.VISIBLE
                mid.visibility = View.GONE
                mid.visibility = View.VISIBLE
                bottom.visibility = View.GONE
                bottom.visibility = View.VISIBLE
            }
            Log.d("LayoutTestFragment", "cost: ${(System.nanoTime() - time) / 1000.0} us")
        }
    }
}