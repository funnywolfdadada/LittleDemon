package com.funnywolf.littledemon.simpleadapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.HashMap

class SimpleAdapter(
    val list: List<*>,
    /**
     * key 是数据的 [Class]，value 是支持对应以 [HolderInfo.layoutRes] 为 key 的 [HolderInfo] 组成的 [SparseArray]
     */
    private val holderArrayMap: Map<Class<out Any>, SparseArray<HolderInfo<out Any>>>,

    /**
     * key 是 [HolderInfo.viewType]，value 是对应支持的 [HolderInfo]
     */
    private val holderArray: SparseArray<HolderInfo<out Any>>
): RecyclerView.Adapter<SimpleAdapter.SimpleHolder<Any>>() {

    var onCreateViewHolderListener: ((SimpleHolder<Any>)->Unit)? = null
    var onBindViewHolderListener: ((SimpleHolder<Any>)->Unit)? = null

    /**
     * 对应 [HolderInfo.viewType] 作为 View Type
     *
     * @param position 数据下标
     * @return 支持对应数据的 [HolderInfo.viewType]，数据为 null 或找不到就返回 0
     */
    override fun getItemViewType(position: Int): Int {
        return getSupportHolderInfo(list[position])?.viewType() ?: 0
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
        val data = list[position] ?: return
        holder.onBindData(data)
        onBindViewHolderListener?.invoke(holder)
    }

    fun <T: Any> getHolderInfo(dataClass: Class<T>, layoutRes: Int): HolderInfo<T>? {
        return getHolderInfoArray(dataClass)?.get(layoutRes) as? HolderInfo<T>
    }

    private fun getSupportHolderInfo(data: Any?): HolderInfo<*>? {
        data ?: return null
        val array = (getHolderInfoArray(data.javaClass) as? SparseArray<HolderInfo<Any>>) ?: return null
        for (i in 0 until array.size()) {
            val info = array.valueAt(i)
            if (info.isSupport(data)) {
                return info
            }
        }
        return null
    }

    private fun getHolderInfoArray(dataClass: Class<out Any>): SparseArray<HolderInfo<out Any>>? {
        return holderArrayMap[dataClass]
            ?: holderArrayMap.keys.firstOrNull {
                it.isAssignableFrom(dataClass)
            }?.let {
                holderArrayMap[it]
            }
    }

    class Builder(private val list: List<*>) {
        private val holderArrayMap = HashMap<Class<out Any>, SparseArray<HolderInfo<out Any>>>()
        private val holderArray = SparseArray<HolderInfo<out Any>>()

        fun <T: Any> add(holderInfo: HolderInfo<T>): Builder {
            val list = holderArrayMap[holderInfo.dataClass]
                ?: SparseArray<HolderInfo<out Any>>().also {
                    holderArrayMap[holderInfo.dataClass] = it
                }
            list.put(holderInfo.layoutRes, holderInfo)
            holderArray.put(holderInfo.viewType(), holderInfo)
            return this
        }

        fun build(): SimpleAdapter {
            return SimpleAdapter(list, holderArrayMap, holderArray)
        }
    }

    class SimpleHolder<T: Any>(v: View, private val holderInfo: HolderInfo<T>) : RecyclerView.ViewHolder(v) {
        private val viewArray = SparseArray<View>()

        /**
         * 方便存储一些 holder 相关的数据
         */
        val holderData = SparseArray<Any>()

        /**
         * 只能在 [onBindData] 里面和之后调用
         */
        lateinit var currentData: T

        init {
            holderInfo.onCreate(this)
        }

        fun onBindData(data: T) {
            currentData = data
            holderInfo.onBind(this)
        }

        inline fun <reified V> getView(id: Int): V {
            return getView(id) as V
        }

        fun getView(id: Int): View {
            return viewArray.get(id) ?: itemView.findViewById<View>(id).also {
                viewArray.put(id, it)
            }
        }

    }

    /**
     * 该类属于 [RecyclerView.ViewHolder] 的配置类，一个实例对应多个 ViewHolder，所以 [onCreate]
     * 和 [onBind] 会传入不同的 ViewHolder。
     * - 继承该类的话，不要再类里面缓存某一个 holder 实例的数据。
     * - 对应 [View] 的缓存可以通过 [SimpleHolder.getView] 获取。
     * - holder 相关的数据请使用 [SimpleHolder.holderData]
     */
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
         * [RecyclerView.Adapter.onCreateViewHolder] 时的回调
         */
        private val onCreate: ((SimpleHolder<T>)->Unit)? = null,

        /**
         * [RecyclerView.Adapter.onBindViewHolder] 时的回调
         */
        private val onBind: ((SimpleHolder<T>)->Unit)? = null,

        /**
         * 可能存在一种数据类型对应多种 [HolderInfo] 的情况，在这里进行过滤
         */
        private val filter: ((T)->Boolean)? = null
    ) {

        open fun viewType(): Int {
            return hashCode()
        }

        open fun onCreate(holder: SimpleHolder<T>) {
            onCreate?.invoke(holder)
        }

        open fun onBind(holder: SimpleHolder<T>) {
            onBind?.invoke(holder)
        }

        open fun isSupport(data: T): Boolean {
            return filter?.invoke(data) != false
        }

        companion object {
            val EMPTY = HolderInfo(Any::class.java, 0)
        }

    }

}
