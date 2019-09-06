package com.funnywolf.littledemon.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.MainActivity
import com.funnywolf.littledemon.R
import kotlinx.android.synthetic.main.fragment_layout_demo_host.*

class DemoHostFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layout_demo_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind(listDemo, ListDemoFragment::class.java)
    }

    private fun bind(v: View, fragmentClass: Class<out Fragment>) {
        v.setOnClickListener {
            MainActivity.startFragment(fragmentClass)
        }
    }

}