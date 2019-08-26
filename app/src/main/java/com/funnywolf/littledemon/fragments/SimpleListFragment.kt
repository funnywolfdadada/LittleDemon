package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.simpleadapter.HolderInfo
import com.funnywolf.littledemon.simpleadapter.SimpleAdapter
import com.funnywolf.littledemon.simpleadapter.SimpleHolder
import kotlinx.android.synthetic.main.fragment_layout_simple_list.*
import kotlinx.android.synthetic.main.view_layout_simple_view_holder.view.*

/**
 * @author zhaodongliang @ Zhihu Inc.
 * @since 2019/4/10
 */
class SimpleListFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_simple_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val holderInfo = object: HolderInfo<String>(String::class.java, R.layout.view_layout_simple_view_holder) {
            override fun onCreateViewHolder(holder: SimpleHolder<String>) {
                holder.itemView.setOnClickListener {
                    Toast.makeText(this@SimpleListFragment.context, "Clicked ${holder.currentData}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onBindViewHolder(holder: SimpleHolder<String>) {
                holder.itemView.content.text = holder.currentData
            }
        }
        val adapter = SimpleAdapter.Builder(getData())
            .add(holderInfo)
            .build()
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
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
}
