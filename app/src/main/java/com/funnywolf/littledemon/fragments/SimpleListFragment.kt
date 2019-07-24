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
import com.funnywolf.littledemon.simpleadapter.Item
import com.funnywolf.littledemon.simpleadapter.SimpleAdapter
import com.funnywolf.littledemon.simpleadapter.SimpleHolderCallback
import kotlinx.android.synthetic.main.fragment_layout_simple_list.*

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
        val adapter = SimpleAdapter(object : SimpleHolderCallback {
            override fun onClick(item: Item) {
                Toast.makeText(this@SimpleListFragment.context, "Clicked ${item.data}", Toast.LENGTH_SHORT).show()
            }
        })
        adapter.list.addAll(getData())
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun getData(): List<Item> {
        val list = ArrayList<Item>()
        for (i in 0 until 20) {
            val count = (Math.random() * 3 + 7).toInt()
            val array = CharArray(count) {
                'A' + (Math.random() * 26).toInt()
            }
            list.add(Item(String(array)))
        }
        return list
    }
}
