package com.funnywolf.littledemon.scene

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.scene.Scene
import com.funnywolf.hollowkit.recyclerview.HolderInfo
import com.funnywolf.hollowkit.recyclerview.LiveList
import com.funnywolf.hollowkit.recyclerview.SimpleAdapter
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.layoutmanager.TagLayoutManagerWithoutRecycler
import com.funnywolf.littledemon.utils.getRandomStrings

class LayoutManagerTestScene: Scene() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.scene_layout_simple_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val liveList = LiveList<String>()
        liveList.addAll(getRandomStrings(200, sizeMin = 1, sizeMax = 5))
        val adapter = SimpleAdapter(liveList.get())
            .addHolderInfo(HolderInfo(String::class.java, R.layout.view_tag, onCreate = { holder ->
                holder.itemView.setOnClickListener { v ->
                    v.isSelected = !v.isSelected
                }
                holder.v<View>(R.id.delete)?.setOnClickListener {
                    liveList.remove(holder.data)
                }
            }, onBind = { holder, data ->
                holder.v<TextView>(R.id.text)?.text = data
            }))
        liveList.bind(adapter)

        findViewById<RecyclerView>(R.id.recyclerView)?.also {
            it.adapter = adapter
            it.layoutManager = TagLayoutManagerWithoutRecycler()
            it.setBackgroundResource(R.drawable.bg0)
        }
    }

}
