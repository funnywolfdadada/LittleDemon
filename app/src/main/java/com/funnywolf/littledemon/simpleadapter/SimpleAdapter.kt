package com.funnywolf.littledemon.simpleadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.funnywolf.littledemon.R
import kotlinx.android.synthetic.main.view_layout_simple_view_holder.view.*

class SimpleAdapter(private val callback: SimpleHolderCallback? = null): RecyclerView.Adapter<SimpleViewHolder>() {
    val list = ArrayList<Item>()

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SimpleViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.view_layout_simple_view_holder, parent, false),
        list[viewType],
        callback)

    override fun getItemCount()
            = list.size

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.bindData(list[position], position)
    }
}

class SimpleViewHolder(
    v: View,
    var data: Item,
    var callback: SimpleHolderCallback?
) : RecyclerView.ViewHolder(v) {

    init {
        v.setOnClickListener {
            callback?.onClick(data)
        }
    }

    fun bindData(data: Item, index: Int) {
        this.data = data
        itemView.content.text = data.data
    }

}

interface SimpleHolderCallback {
    fun onClick(item: Item)
}

data class Item(val data: String)
