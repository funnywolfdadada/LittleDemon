package com.funnywolf.littledemon.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.simpleadapter.HolderInfo
import com.funnywolf.littledemon.simpleadapter.SimpleAdapter
import com.funnywolf.littledemon.simpleadapter.SimpleHolder
import com.funnywolf.littledemon.utils.dp2pix
import kotlinx.android.synthetic.main.fragment_layout_drawer.*

class DrawerListFragment: Fragment() {
    private val recyclerHeight: Int by lazy {
        page.bottom - header.bottom - dp2pix(context!!, 140)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_drawer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        header.setOnClickListener { clickHeader() }
        recycler_background.setOnClickListener { closeDrawer() }
        recycler_view.setOnClickListener { closeDrawer() }

        val adapter = SimpleAdapter.Builder(getData())
            .add(object: HolderInfo<String>(String::class.java, R.layout.view_layout_simple_view_holder) {
                override fun onCreateViewHolder(holder: SimpleHolder<String>) {
                    holder.itemView.setOnClickListener {
                        closeDrawer()
                        header.text = holder.currentData
                    }
                }
            }).build()
        recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view.adapter = adapter
    }

    private fun getData(): List<String> {
        val list = ArrayList<String>()
        for (i in 0 until 20) {
            val count = (Math.random() * 3 + 7).toInt()
            val array = CharArray(count) {
                'A' + (Math.random() * 26).toInt()
            }
            list.add(String(array))
        }
        return list
    }

    private fun clickHeader() {
        openDrawer()
    }

    private fun openDrawer() {
        recycler_background.animate().alpha(1.0f).withStartAction {
            recycler_background.visibility = View.VISIBLE
        }.start()
        val animator = ValueAnimator.ofInt(recycler_view.height, recyclerHeight)
        animator.addUpdateListener {
            val currentHeight = it.animatedValue as? Int ?: return@addUpdateListener
            if (currentHeight > 0 && recycler_view.visibility != View.VISIBLE) {
                recycler_view.visibility = View.VISIBLE
            }
            recycler_view.layoutParams.height = if(currentHeight == recyclerHeight) {
                ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                currentHeight
            }
            recycler_view.layoutParams = recycler_view.layoutParams
        }
        animator.start()
    }

    private fun closeDrawer() {
        recycler_background.animate().alpha(0.0f).withEndAction {
            recycler_background.visibility = View.GONE
        }
        val animator = ValueAnimator.ofInt(recycler_view.height, 0)
        animator.addUpdateListener {
            val currentHeight = it.animatedValue as? Int ?: return@addUpdateListener
            recycler_view.layoutParams.height = currentHeight
            recycler_view.layoutParams = recycler_view.layoutParams
            if (currentHeight == 0 && recycler_view.visibility != View.GONE) {
                recycler_view.visibility = View.GONE
            }
        }
        animator.start()
    }
}