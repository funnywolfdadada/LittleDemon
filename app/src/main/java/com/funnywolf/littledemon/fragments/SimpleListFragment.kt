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
import com.funnywolf.littledemon.simpleadapter.SimpleAdapter
import com.funnywolf.littledemon.utils.getRandomInt
import com.funnywolf.littledemon.utils.getRandomStrings
import com.funnywolf.littledemon.utils.RecyclerViewLoadMore
import kotlinx.android.synthetic.main.fragment_layout_simple_list.*

class SimpleListFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_simple_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = ArrayList<Any>()
        list.addAll(getRandomStrings(20))
        list.addAll(getRandomInt(20))

        val adapter = SimpleAdapter.Builder(list)
            .add(SimpleAdapter.HolderInfo(String::class.java, R.layout.view_layout_simple_view_holder, { holder ->
                holder.itemView.setOnClickListener {
                    Toast.makeText(this@SimpleListFragment.context, "Clicked ${holder.currentData}", Toast.LENGTH_SHORT).show()
                    val index = list.indexOf(holder.currentData)
                    if (index > 0) {
                        list.removeAt(index)
                        recyclerView.adapter?.notifyItemRemoved(index)
                    }
                }
            }, { holder ->
                holder.getView<TextView>(R.id.content).text = holder.currentData
            }))
            .add(SimpleAdapter.HolderInfo(Integer::class.java, R.layout.view_layout_simple_view_holder, { holder ->
                holder.itemView.setOnClickListener {
                    Toast.makeText(this@SimpleListFragment.context, "Clicked ${holder.currentData}", Toast.LENGTH_SHORT).show()
                    val index = list.indexOf(holder.currentData)
                    if (index > 0) {
                        list.removeAt(index)
                        recyclerView.adapter?.notifyItemRemoved(index)
                    }
                }
            }, { holder ->
                holder.getView<TextView>(R.id.content).text = holder.currentData.toString()
            }))
            .build()

        root.clipChildren = false
        recyclerView.clipChildren = false
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        RecyclerViewLoadMore(recyclerView, 200) {
            Toast.makeText(this@SimpleListFragment.context, "Load More", Toast.LENGTH_SHORT).show()
        }
    }

}
