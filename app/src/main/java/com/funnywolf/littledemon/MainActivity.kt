package com.funnywolf.littledemon

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.funnywolf.littledemon.fragments.*
import com.funnywolf.littledemon.utils.LiveDataBus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private val fragmentData = LiveDataBus.get().getData(Fragment::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LiveDataBus.get().observe(Fragment::class.java, this, Observer {
            openFragment(it ?: return@Observer)
        })
        initViews()
    }

    override fun onResume() {
        super.onResume()

        // 测试并发
        val stopTime = System.nanoTime() + 1000 * 1000 * 1000
        repeat(10) {
            Thread {
                while (System.nanoTime() < stopTime) {}
                Log.d(TAG, "$it: ${Thread.currentThread()} ${LiveDataBus.get(TAG).hashCode()}")
            }.start()
        }

    }

    private fun initViews() {
        dragClose.setOnClickListener {
//            fragmentData.postValue(DragCloseFragment())
            LiveDataBus.get().post(Fragment::class.java, DragCloseFragment())
        }

        dragExtend.setOnClickListener {
            fragmentData.postValue(DragExtendFragment())
        }

        nestScrollView.setOnClickListener {
            fragmentData.postValue(NestScrollViewFragment())
        }

        constrainLayoutTest.setOnClickListener {
            fragmentData.postValue(ConstraintLayoutTestFragment())
        }

        scrollAndDragExtend.setOnClickListener {
            fragmentData.postValue(ScrollAndDragExtentFragment())
        }

        simpleList.setOnClickListener {
            fragmentData.postValue(SimpleListFragment())
        }

        fragmentViewPager.setOnClickListener {
            fragmentData.postValue(FragmentViewPagerFragment())
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
