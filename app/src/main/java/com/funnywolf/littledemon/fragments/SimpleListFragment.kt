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
        val adapter = SimpleAdapter()
        adapter.list.addAll(getData())
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

class SimpleAdapter: RecyclerView.Adapter<SimpleViewHolder>() {
    val list = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = SimpleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_layout_simple_view_holder, parent, false))

    override fun getItemCount()
            = list.size

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.bindData(list[position], position)
    }
}

class SimpleViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    var data: String = ""

    init {
        v.setOnClickListener {
            Toast.makeText(v.context, "click $data", Toast.LENGTH_SHORT).show()
        }
    }

    fun bindData(data: String, index: Int) {
        this.data = data
        itemView.content.text = "$index: $data"
    }
}