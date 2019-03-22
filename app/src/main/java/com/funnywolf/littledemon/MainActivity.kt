package com.funnywolf.littledemon

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.funnywolf.littledemon.fragments.ConstraintLayoutTestFragment
import com.funnywolf.littledemon.fragments.DragCloseFragment
import com.funnywolf.littledemon.fragments.DragExtendFragment
import com.funnywolf.littledemon.fragments.NestScrollViewFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {
        dragClose.setOnClickListener {
            openFragment(DragCloseFragment())
        }

        dragExtend.setOnClickListener {
            openFragment(DragExtendFragment())
        }

        nestScrollView.setOnClickListener {
            openFragment(NestScrollViewFragment())
        }

        constrainLayoutTest.setOnClickListener {
            openFragment(ConstraintLayoutTestFragment())
        }

    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_content, fragment)
            .addToBackStack(null)
            .commit()
    }
}
