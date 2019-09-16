package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.layoutmanager.TagLayoutManager
import com.funnywolf.littledemon.layoutmanager.TagLayoutManagerWithoutRecycler
import com.funnywolf.littledemon.live.LiveList
import com.funnywolf.littledemon.simpleadapter.SimpleAdapter
import com.funnywolf.littledemon.utils.getRandomStrings
import kotlinx.android.synthetic.main.fragment_layout_simple_list.*

class LayoutManagerTestFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layout_simple_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val liveList = LiveList<String>()
        liveList.addAll(mutableListOf("A", "BC", "DEF"))
        liveList.addAll(getRandomStrings(100, false, sizeMin = 1, sizeMax = 5))
        val adapter = SimpleAdapter.Builder(liveList.get())
            .add(SimpleAdapter.HolderInfo(String::class.java, R.layout.view_tag, { holder ->
                holder.itemView.setOnClickListener { v ->
                    v.isSelected = !v.isSelected
                }
                holder.getView(R.id.delete).setOnClickListener {
                    liveList.remove(holder.currentData)
                }
            }, {
                it.getView<TextView>(R.id.text).text = it.currentData
            }))
            .build()
        liveList.bind(adapter)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = TagLayoutManager()
        recyclerView.setBackgroundResource(R.mipmap.bg0)
    }

}
