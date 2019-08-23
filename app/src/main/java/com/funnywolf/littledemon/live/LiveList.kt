package com.funnywolf.littledemon.live

import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

interface LiveListSource<T> {
    fun get(): List<T>
    fun bind(adapter: RecyclerView.Adapter<*>)
    fun unbind()
}

interface MutableListSource<T>: LiveListSource<T> {
    fun update(func: ((MutableList<T>)->Unit)? = null)
    fun add(data: T, index: Int? = null)
    fun addAll(c: Collection<T>, index: Int? = null)
    fun remove(data: T)
    fun removeAt(index: Int)
    fun removeAll(c: Collection<T>)
    fun set(index: Int, data: T)
    fun clearAndSet(c: Collection<T>)
    fun clear()
}

class LiveList<T>: MutableListSource<T> {

    private val rawList: MutableList<T> = ArrayList()
    private var adapterRef: WeakReference<RecyclerView.Adapter<*>>? = null

    override fun get(): List<T> = rawList

    override fun bind(adapter: RecyclerView.Adapter<*>) {
        adapterRef = WeakReference(adapter)
    }

    override fun unbind() {
        adapterRef?.clear()
        adapterRef = null
    }

    override fun update(func: ((MutableList<T>) -> Unit)?) {
        func?.invoke(rawList)
        adapterRef?.get()?.notifyDataSetChanged()
    }

    override fun add(data: T, index: Int?) {
        if (index == null) {
            rawList.add(data)
            adapterRef?.get()?.notifyItemInserted(rawList.size - 1)
        } else {
            rawList.add(index, data)
            adapterRef?.get()?.notifyItemInserted(index)
        }
    }

    override fun addAll(c: Collection<T>, index: Int?) {
        if (index == null) {
            rawList.addAll(c)
            adapterRef?.get()?.notifyItemRangeInserted(rawList.size - c.size - 1, c.size)
        } else {
            rawList.addAll(index, c)
            adapterRef?.get()?.notifyItemRangeInserted(index, c.size)
        }
    }

    override fun remove(data: T) {
        rawList.removeAt(rawList.indexOf(data))
    }

    override fun removeAt(index: Int) {
        if (index < 0 || index >= rawList.size) { return }
        rawList.removeAt(index)
        adapterRef?.get()?.notifyItemRemoved(index)
    }

    override fun removeAll(c: Collection<T>) {
        rawList.removeAll(c)
        adapterRef?.get()?.notifyDataSetChanged()
    }

    override fun set(index: Int, data: T) {
        if (index < 0 || index >= rawList.size) { return }
        rawList[index] = data
        adapterRef?.get()?.notifyItemChanged(index)
    }

    override fun clearAndSet(c: Collection<T>) {
        rawList.clear()
        rawList.addAll(c)
        adapterRef?.get()?.notifyDataSetChanged()
    }

    override fun clear() {
        rawList.clear()
        adapterRef?.get()?.notifyDataSetChanged()
    }

}
