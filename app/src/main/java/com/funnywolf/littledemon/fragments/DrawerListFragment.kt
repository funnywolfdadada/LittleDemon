package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.simpleadapter.Item
import com.funnywolf.littledemon.simpleadapter.SimpleAdapter
import com.funnywolf.littledemon.simpleadapter.SimpleHolderCallback

class DrawerListFragment: Fragment(), SimpleHolderCallback {
    private lateinit var header: TextView
    private lateinit var content: View
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_drawer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        header = view.findViewById(R.id.header)
        header.setOnClickListener { clickHeader() }
        content = view.findViewById(R.id.content)
        content.setOnClickListener { closeDrawer() }
        recyclerView = view.findViewById(R.id.recycler_view)

        val adapter = SimpleAdapter(this)
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

    override fun onClick(item: Item) {
        closeDrawer()
        header.text = item.data
    }

    private fun clickHeader() {
        openDrawer()
    }

    private fun openDrawer() {
        content.visibility = View.VISIBLE
        recyclerView.animate().y(0.0f).start()
    }

    private fun closeDrawer() {
        recyclerView.animate().y(-recyclerView.height.toFloat()).withEndAction {
            content.visibility = View.GONE
        }.start()
    }
}