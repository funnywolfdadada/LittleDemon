package com.funnywolf.littledemon.simpleadapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SimpleAdapter(
    val list: List<*>,
    /**
     * key 是数据的 [Class]，value 是支持对应数据类的 [HolderInfo] 的列表
     */
    private val holderListMap: Map<Class<out Any>, List<HolderInfo<out Any>>>,

    /**
     * key 是 [SimpleAdapter.viewType]，value 是对应支持的 [HolderInfo]
     */
    private val holderArray: SparseArray<HolderInfo<out Any>?>
): RecyclerView.Adapter<SimpleAdapter.SimpleHolder<Any>>() {

    var dispatcher: ((Any) -> HolderInfo<Any>?)? = null
    var onCreateViewHolderListener: ((SimpleHolder<Any>)->Unit)? = null
    var onBindViewHolderListener: ((SimpleHolder<Any>)->Unit)? = null

    /**
     * 对应 [SimpleAdapter.viewType] 作为 View Type
     *
     * @param position 数据下标
     * @return 支持对应数据的 [SimpleAdapter.viewType]，数据为 null 或找不到就返回 0
     */
    override fun getItemViewType(position: Int): Int {
        return viewType(getHolderInfo(list[position] ?: return 0))
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

    companion object {

        fun viewType(layoutRes: Int, dataClass: Class<*>): Int {
            return Objects.hash(layoutRes, dataClass)
        }

        fun viewType(holderInfo: HolderInfo<*>?): Int {
            holderInfo ?: return 0
            return viewType(holderInfo.layoutRes, holderInfo.dataClass)
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
            holderArray.append(viewType(holderInfo), holderInfo)
            return this
        }

        fun build(): SimpleAdapter {
            return SimpleAdapter(list, holderListMap, holderArray)
        }
    }

    class SimpleHolder<T: Any>(v: View, private val holderInfo: HolderInfo<T>) : RecyclerView.ViewHolder(v) {
        private val viewArray = SparseArray<View>()

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

        inline fun <reified V> getView(id: Int): V {
            return getView(id) as V
        }

        fun getView(id: Int): View? {
            return viewArray.get(id) ?: itemView.findViewById<View>(id).also {
                viewArray.put(id, it)
            }
        }

    }

    /**
     * 该类属于 [RecyclerView.ViewHolder] 的配置类，一个实例对应多个 ViewHolder，所以 [onCreateViewHolder]
     * 和 [onBindViewHolder] 会传入不同的 ViewHolder。同样的，继承该类的话，不要再类里面缓存某一个 ViewHolder
     * 实例的数据，对应 [View] 的缓存可以通过 [SimpleHolder.getView] 获取
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
         * create 时的回调
         */
        private val onCreateViewHolder: ((SimpleHolder<T>)->Unit)? = null,

        /**
         * bind 时的回调
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

}
