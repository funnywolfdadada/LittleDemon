package com.funnywolf.littledemon.tmp

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

class SimpleAdapter(
    val list: List<*>,
    /**
     * key 是数据的 [Class.hashCode]，value 是支持对应数据类的 [HolderInfo] 的列表
     */
    private val holderListArray: SparseArray<List<HolderInfo<Any>>?>,

    /**
     * key 是 [HolderInfo.holderClass] 的 hashCode，value 是支持对应的 [HolderInfo]
     */
    private val holderArray: SparseArray<HolderInfo<Any>?>
): RecyclerView.Adapter<BaseSimpleHolder<Any>>() {

    /**
     * 对应 [HolderInfo.holderClass] 的 hashCode 作为 View Type
     *
     * @param position 数据下标
     * @return 支持对应数据的 [HolderInfo.holderClass] 的 hashCode，数据为 null 或找不到就返回 0
     */
    override fun getItemViewType(position: Int): Int {
        val data = list[position] ?: return 0
        val dataClass = data.javaClass
        return holderListArray[dataClass.hashCode()]
            ?.firstOrNull { it.isThisOne?.invoke(data) != false }
            ?.holderClass
            .hashCode()
    }

    /**
     * 创建 ViewHolder，
     *
     * @param parent 父 [View]
     * @param viewType [HolderInfo.holderClass] 的 hashCode，0 表示数据为 null 或不支持
     * @return 对应 [HolderInfo.holderClass] 的实例
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSimpleHolder<Any> {
        val holderInfo = holderArray[viewType]
        if (viewType <= 0 || holderInfo == null) {
            return NullHolder(parent.context)
        }
        return try {
            val view = LayoutInflater.from(parent.context).inflate(holderInfo.layoutRes, parent, false)
            val holder = holderInfo.holderClass.getConstructor(View::class.java).newInstance(view)

            holder
        } catch (e: Exception) {
            NullHolder(parent.context)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseSimpleHolder<Any>, position: Int) {
        list[position]?.also {
            holder.onBindDataInternal(it)
        }
    }

}

interface HolderListener {
    fun onCreateViewHolder(holder: BaseSimpleHolder<*>)
}

data class HolderInfo<T: Any>(
    /**
     * 数据类型
     */
    val dataClass: Class<T>,
    /**
     * 对应 [BaseSimpleHolder] 的类型
     */
    val holderClass: Class<out BaseSimpleHolder<T>>,
    /**
     * 布局文件
     */
    @LayoutRes val layoutRes: Int,
    /**
     * 是否支持该数据
     */
    val isThisOne: ((T) -> Boolean)?
)



abstract class BaseSimpleHolder<T: Any>(v: View) : RecyclerView.ViewHolder(v) {
    /**
     * 只能在 [onBindData] 里面和之后调用
     */
    lateinit var currentData: T
    fun onBindDataInternal(data: T) {
        currentData = data
        try {
            onBindData(data)
        } catch (e: Exception) {
        }
    }
    abstract fun onBindData(data: T)
}

class NullHolder(context: Context): BaseSimpleHolder<Any>(View(context)) {
    override fun onBindData(data: Any) {}
}
