package com.funnywolf.littledemon.tmp

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

class SimpleAdapter(
    val list: List<*>,
    /**
     * key 是数据的 [Class]，value 是支持对应数据类的 [HolderInfo] 的列表
     */
    private val holderListMap: Map<Class<*>, List<HolderInfo<*>>>,

    /**
     * key 是 [HolderInfo] 的 [Class.hashCode]，value 是支持对应的 [HolderInfo]
     */
    private val holderArray: SparseArray<HolderInfo<*>?>
): RecyclerView.Adapter<SimpleHolder<Any>>() {

    var dispatcher: ((Any) -> HolderInfo<Any>?)? = null
    var onCreateViewHolderListener: ((SimpleHolder<Any>)->Unit)? = null
    var onBindViewHolderListener: ((SimpleHolder<Any>)->Unit)? = null

    /**
     * 对应 [HolderInfo] 的 [Class.hashCode] 作为 View Type
     *
     * @param position 数据下标
     * @return 支持对应数据的 [HolderInfo] 的 [Class.hashCode]，数据为 null 或找不到就返回 0
     */
    override fun getItemViewType(position: Int): Int {
        return getHolderInfo(list[position] ?: return 0).hashCode()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleHolder<Any> {
        val holderInfo = (holderArray[viewType] as? HolderInfo<Any>) ?: HolderInfo.EMPTY
        val view = if (holderInfo != HolderInfo.EMPTY) {
            LayoutInflater.from(parent.context).inflate(holderInfo.layoutRes, parent, false)
        } else {
            View(parent.context)
        }
        val holder = SimpleHolder(view, holderInfo)
        onCreateViewHolderListener?.invoke(holder)
        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SimpleHolder<Any>, position: Int) {
        val data = list[position]
        if (data != null) {
            holder.onBindViewHolder(data)
            onBindViewHolderListener?.invoke(holder)
        }
    }

    private fun getHolderInfo(data: Any): HolderInfo<*>? {
        // 拿到对应的 HolderInfo 列表，没有就返回 null
        val holderList = getHolderList(data.javaClass) ?: return null
        // dispatcher 不存在，或不支持，就返回 holderList 第一个
        val dispatchedHolder = dispatcher?.invoke(data) ?: return holderList.firstOrNull()
        // dispatchedHolder 注册过就直接返回，否则返回 null
        return if (holderList.contains(dispatchedHolder)) {
            dispatchedHolder
        } else {
            null
        }
    }

    private fun getHolderList(dataClass: Class<Any>): List<HolderInfo<*>>? {
        return holderListMap[dataClass]
            ?: holderListMap.keys.firstOrNull {
                it.isAssignableFrom(dataClass)
            }?.let {
                holderListMap[it]
            }
    }

    class Builder(private val list: List<*>) {
        private val holderListMap = HashMap<Class<out Any>, MutableList<HolderInfo<out Any>>>()
        private val holderArray = SparseArray<HolderInfo<out Any>?>()

        fun <T: Any> add(holderInfo: HolderInfo<T>): Builder {
            val list = holderListMap[holderInfo.dataClass]
                ?: ArrayList<HolderInfo<out Any>>().also {
                    holderListMap[holderInfo.dataClass] = it
                }
            list.add(holderInfo)
            holderArray.append(holderInfo.javaClass.hashCode(), holderInfo)
            return this
        }

        fun build(): SimpleAdapter {
            return SimpleAdapter(list, holderListMap, holderArray)
        }
    }
}

class SimpleHolder<T: Any>(v: View, private val holderInfo: HolderInfo<T>) : RecyclerView.ViewHolder(v) {

    /**
     * 只能在 [onBindViewHolder] 里面和之后调用
     */
    lateinit var currentData: T

    init {
        holderInfo.onCreateViewHolder(this)
    }

    fun onBindViewHolder(data: T) {
        currentData = data
        holderInfo.onBindViewHolder(this)
    }

}

open class HolderInfo<T: Any> (
    /**
     * 数据类型
     */
    val dataClass: Class<T>,

    /**
     * 布局文件
     */
    @LayoutRes val layoutRes: Int,

    /**
     * 创建
     */
    private val onCreateViewHolder: ((SimpleHolder<T>)->Unit)? = null,

    /**
     * 绑定
     */
    private val onBindViewHolder: ((SimpleHolder<T>)->Unit)? = null
) {
    open fun onCreateViewHolder(holder: SimpleHolder<T>) {
        onCreateViewHolder?.invoke(holder)
    }

    open fun onBindViewHolder(holder: SimpleHolder<T>) {
        onBindViewHolder?.invoke(holder)
    }

    companion object {
        val EMPTY = HolderInfo(Any::class.java, 0)
    }
}
