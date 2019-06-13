package com.funnywolf.littledemon.simpleadapter

import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface AdapterOperation {

}

typealias IsSupport = (Any) -> Boolean
typealias OnCreate = (View) -> Void
typealias OnBindData = (SimpleViewHolder, Any) -> Void

private data class Item(var data: Any, var container: Container? = null)

open class Container(val layoutId: Int, val dataClass: Class<*>,
                        val isSupport: IsSupport, val onBindData: OnBindData)

class SimpleViewHolder(v: View): RecyclerView.ViewHolder(v) {

}

class SimpleAdapter: RecyclerView.Adapter<SimpleViewHolder>() {
    private val containersArray: SparseArray<List<Container>> = SparseArray()
    private val list: List<Item> = ArrayList()

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val container = getContainer(viewType)
        if (container == null) {
            Log.d("", "")
            return SimpleViewHolder(View(parent.context))
        }
        return SimpleViewHolder(LayoutInflater.from(parent.context).inflate(container.layoutId, parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        getContainer(position)?.onBindData?.invoke(holder, list[position].data)
    }

    private fun getContainer(position: Int): Container? {
        val data = list[position].data
        return (containersArray.get(data.javaClass.hashCode()) ?: containersArray.get(0))
            ?.find { it.isSupport(data) }
    }
}

