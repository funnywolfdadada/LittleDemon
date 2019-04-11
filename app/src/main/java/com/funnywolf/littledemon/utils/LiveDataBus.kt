package com.funnywolf.littledemon.utils

import androidx.lifecycle.MutableLiveData
import java.util.concurrent.ConcurrentHashMap

/**
 * @author zhaodongliang @ Zhihu Inc.
 * @since 2019/4/11
 */
class LiveDataBus private constructor(val tag: String) {
    companion object {

        /**
         * 默认总线
         */
        private val INSTANCE = LiveDataBus("")

        /**
         * 存储以 {@link ##mTag} 标识的总线
         * key: tag 字符串
         * value: 对应的总线
         */
        private val BUSES = ConcurrentHashMap<String, LiveDataBus>(2)

        /**
         * 返回默认总线
         */
        fun get(): LiveDataBus = get("")

        /**
         * 根据 tag 拿对应总线
         * @param tag 标识事件总线的 tag
         * @return 返回 tag 标识的总线，tag 为 null 或空字符串时返回默认总线
         */
        fun get(tag: String): LiveDataBus {
            if (tag == "") {
                return INSTANCE
            }
            return BUSES[tag] ?: synchronized(BUSES) {
                var bus = BUSES[tag]
                if (bus == null) {
                    bus = LiveDataBus(tag)
                    BUSES[tag] = bus
                }
                return bus
            }
        }
    }


    /**
     * 存储事件类型和 MutableLiveData
     * key: 事件的 class
     * value: 包含事件 class 和 MutableLiveData 的 Item
     */
    private val array = ConcurrentHashMap<Class<*>, Item<*>>()

    /**
     * 返回事件 T 的 MutableLiveData
     */
    fun <T> getData(clazz: Class<T>) = getItem(clazz).mutableLiveData

    private fun <T> getItem(clazz: Class<T>): Item<T> {
        return getItemInternal(clazz) ?: synchronized(this) {
            var item = getItemInternal(clazz)
            if (item == null) {
                item = Item(clazz)
                array[clazz] = item
            }
            return item
        }
    }

    private fun <T> getItemInternal(clazz: Class<T>): Item<T>? {
        val item = array[clazz]
        if (item != null && item.clazz == clazz) {
            return item as Item<T>
        }
        return null
    }

    /**
     * 存储事件类型和 MutableLiveData，为了保证事件 class 的泛型和 MutableLiveData 的泛型一致
     */
    class Item<T>(val clazz: Class<T>) {
        val mutableLiveData: MutableLiveData<T> = MutableLiveData()
    }
}