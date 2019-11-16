package com.funnywolf.littledemon.utils

import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

interface LiveListSource<T> {
    fun get(): List<T>
    fun bind(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>)
    fun unbind()
}

interface MutableListSource<T>: LiveListSource<T> {
    fun update(func: ((MutableList<T>)->Unit)? = null)
    fun update(index: Int, func: ((T)->Unit)? = null)
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
    private var adapterRef: WeakReference<RecyclerView.Adapter<out RecyclerView.ViewHolder>>? = null

    override fun get(): List<T> = rawList

    override fun bind(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
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

    override fun update(index: Int, func: ((T) -> Unit)?) {
        if (safeIndex(index)) {
            func?.invoke(rawList[index])
            adapterRef?.get()?.notifyItemChanged(index)
        }
    }

    override fun add(data: T, index: Int?) {
        if (index == null) {
            rawList.add(data)
            adapterRef?.get()?.notifyItemInserted(rawList.size - 1)
        } else if (safeAddIndex(index)) {
            rawList.add(index, data)
            adapterRef?.get()?.notifyItemInserted(index)
        }
    }

    override fun addAll(c: Collection<T>, index: Int?) {
        if (index == null) {
            rawList.addAll(c)
            adapterRef?.get()?.notifyItemRangeInserted(rawList.size - c.size - 1, c.size)
        } else if (safeAddIndex(index)) {
            rawList.addAll(index, c)
            adapterRef?.get()?.notifyItemRangeInserted(index, c.size)
        }
    }

    override fun remove(data: T) {
        removeAt(rawList.indexOf(data))
    }

    override fun removeAt(index: Int) {
        if (safeIndex(index)) {
            rawList.removeAt(index)
            adapterRef?.get()?.notifyItemRemoved(index)
        }
    }

    override fun removeAll(c: Collection<T>) {
        rawList.removeAll(c)
        adapterRef?.get()?.notifyDataSetChanged()
    }

    override fun set(index: Int, data: T) {
        if (safeIndex(index)) {
            rawList[index] = data
            adapterRef?.get()?.notifyItemChanged(index)
        }
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

    /**
     * 删、改、查的 [index] 是否安全
     */
    private fun safeIndex(index: Int) = index >= 0 && index < rawList.size

    /**
     * 增 的 [index] 是否安全
     */
    private fun safeAddIndex(index: Int) = index >= 0 && index <= rawList.size
}
