package com.funnywolf.littledemon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.fragments.*
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

        scrollAndDragExtend.setOnClickListener {
            openFragment(ScrollAndDragExtentFragment())
        }

        simpleList.setOnClickListener {
            openFragment(SimpleListFragment())
        }

        fragmentViewPager.setOnClickListener {
            openFragment(FragmentViewPagerFragment())
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
