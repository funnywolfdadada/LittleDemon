package com.funnywolf.littledemon

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.funnywolf.littledemon.fragments.DragCloseFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {
        dragClose.setOnClickListener(this::openDragCloseTest)
    }

    private fun openDragCloseTest(v: View) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_content, DragCloseFragment())
            .commit()
    }
}
