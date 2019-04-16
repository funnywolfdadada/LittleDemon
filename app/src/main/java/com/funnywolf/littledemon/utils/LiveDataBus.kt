package com.funnywolf.littledemon.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
         * @return 返回 tag 标识的总线，tag 为空字符串时返回默认总线
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
                return@get bus
            }
        }
    }


    /**
     * 存储事件类型和 MutableLiveData
     * key: 事件的 class
     * value: 包含事件 class 和 MutableLiveData 的 LiveDataItem
     */
    private val map = ConcurrentHashMap<Class<*>, LiveDataItem<*>>()

    /**
     * 返回事件 T 的 MutableLiveData
     */
    fun <T> getData(clazz: Class<T>) = getItem(clazz).mutableLiveData

    fun <T> observe(clazz: Class<T>, owner: LifecycleOwner, observer: Observer<T>) {
        getData(clazz).observe(owner, observer)
    }

    fun <T> observeForever(clazz: Class<T>, observer: Observer<T>) {
        getData(clazz).observeForever(observer)
    }

    fun <T> removeObserver(clazz: Class<T>, observer: Observer<T>) {
        getData(clazz).removeObserver(observer)
    }

    fun <T> post(clazz: Class<T>, data: T?) {
        getData(clazz).postValue(data)
    }

    fun <T : Any> post(data: T) {
        getData(data.javaClass).postValue(data)
    }

    private fun <T> getItem(clazz: Class<T>): LiveDataItem<T> {
        return getItemInternal(clazz) ?: synchronized(this) {
            var item = getItemInternal(clazz)
            if (item == null) {
                item = LiveDataItem(clazz)
                map[clazz] = item
            }
            return@getItem item
        }
    }

    private fun <T> getItemInternal(clazz: Class<T>): LiveDataItem<T>? {
        val item = map[clazz]
        if (item != null && item.clazz == clazz) {
            return item as LiveDataItem<T>
        }
        return null
    }

    /**
     * 存储事件类型和 MutableLiveData，为了保证事件 class 的泛型和 MutableLiveData 的泛型一致
     */
    class LiveDataItem<T>(val clazz: Class<T>) {
        val mutableLiveData: MutableLiveData<T> = MutableLiveData()
    }
}